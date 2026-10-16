#!/usr/bin/env python3
"""Fails when an item definition, model or texture the mod ships points at something that does not exist.

Checks, for every namespace under src/main/resources/assets:
  * items/*.json      -> every referenced model (minecraft:model "model", minecraft:special "base") exists
  * models/**/*.json  -> parents resolve, every texture variable resolves to a PNG,
                         every "#ref" used by an element face is defined, the model has a particle texture,
                         and every texture sits in an atlas (26.x item models only see atlas sprites)
Vanilla (minecraft:) models and textures are assumed to exist.

Usage: check_assets.py [resources_dir]
"""
import json
import pathlib
import sys

RES = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else "src/main/resources")
ASSETS = RES / "assets"

# Vanilla 26.3 atlas directory sources (assets/minecraft/atlases/{items,blocks}.json); they apply to every namespace.
ATLAS_DIRS = ["item/", "block/", "entity/conduit/"]
# Vanilla parents that define "particle" from "layer0".
GENERATED = {"item/generated", "item/handheld", "item/handheld_rod", "item/handheld_mace"}
REMOVED_PARENTS = {"builtin/entity", "builtin/generated"}

errors = []


def ident(s, default_ns="minecraft"):
    ns, _, path = s.partition(":") if ":" in s else (default_ns, "", s)
    return ns, path


def load(path):
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except Exception as e:  # noqa: BLE001
        errors.append(f"{path.relative_to(RES)}: invalid JSON ({e})")
        return None


# --- atlas sprites the mod adds (assets/*/atlases/*.json, merged into the vanilla atlases)
atlas_singles, atlas_dirs = set(), list(ATLAS_DIRS)
for f in ASSETS.glob("*/atlases/*.json"):
    for src in (load(f) or {}).get("sources", []):
        t = src.get("type", "").removeprefix("minecraft:")
        if t == "single":
            atlas_singles.add(ident(src["resource"]))
        elif t == "directory":
            atlas_dirs.append(src["source"].rstrip("/") + "/")


def texture_ok(ref, where):
    ns, path = ident(ref)
    if ns == "minecraft":
        return
    png = ASSETS / ns / "textures" / (path + ".png")
    if not png.is_file():
        errors.append(f"{where}: texture {ns}:{path} -> missing {png.relative_to(RES)}")
        return
    if (ns, path) not in atlas_singles and not any(path.startswith(d) for d in atlas_dirs):
        errors.append(f"{where}: texture {ns}:{path} is not in any atlas (move it under textures/item/ "
                      f"or add it to assets/minecraft/atlases/items.json)")


def model_file(ns, path):
    return ASSETS / ns / "models" / (path + ".json")


def resolve(ns, path, where, seen=()):
    """Returns (textures, has_elements, generated) merged along the parent chain, or None if broken."""
    if (ns, path) in seen:
        errors.append(f"{where}: parent loop at {ns}:{path}")
        return None
    if ns == "minecraft":
        if path in REMOVED_PARENTS:
            errors.append(f"{where}: parent minecraft:{path} no longer exists in 26.x")
            return None
        return ({}, True, path in GENERATED)
    f = model_file(ns, path)
    if not f.is_file():
        errors.append(f"{where}: model {ns}:{path} -> missing {f.relative_to(RES)}")
        return None
    m = load(f)
    if m is None:
        return None
    textures, has_elements, generated = {}, "elements" in m, False
    if "parent" in m:
        pns, ppath = ident(m["parent"], "minecraft")
        parent = resolve(pns, ppath, where, seen + ((ns, path),))
        if parent is None:
            return None
        textures, p_elements, generated = dict(parent[0]), parent[1], parent[2]
        has_elements = has_elements or p_elements
    textures.update(m.get("textures", {}))
    return textures, has_elements, generated, m


def check_model(ns, path):
    where = f"{ns}:{path}"
    r = resolve(ns, path, where)
    if r is None:
        return
    textures, _, generated = r[0], r[1], r[2]
    model = r[3] if len(r) > 3 else {}

    def follow(value, depth=0):
        while isinstance(value, str) and value.startswith("#") and depth < 16:
            value, depth = textures.get(value[1:]), depth + 1
        return value

    for key, value in textures.items():
        final = follow(value)
        if final is None:
            errors.append(f"{where}: texture variable '{key}' -> undefined {value}")
        else:
            texture_ok(final, where)

    for el in model.get("elements", []):
        for face in el.get("faces", {}).values():
            t = face.get("texture", "")
            if t.startswith("#") and follow(t) is None:
                errors.append(f"{where}: face texture {t} is not defined")

    if "particle" not in textures and not (generated and "layer0" in textures):
        errors.append(f"{where}: no particle texture (26.x requires one; add \"textures\": {{\"particle\": ...}})")


def walk_definition(node, refs):
    if isinstance(node, dict):
        t = node.get("type", "").removeprefix("minecraft:")
        if t == "model" and "model" in node:
            refs.append(node["model"])
        if t == "special" and "base" in node:
            refs.append(node["base"])
        for v in node.values():
            walk_definition(v, refs)
    elif isinstance(node, list):
        for v in node:
            walk_definition(v, refs)


for ns_dir in sorted(p for p in ASSETS.iterdir() if p.is_dir()):
    ns = ns_dir.name
    for f in sorted((ns_dir / "items").glob("*.json")):
        refs = []
        walk_definition(load(f) or {}, refs)
        if not refs:
            errors.append(f"{f.relative_to(RES)}: item definition references no model")
        for ref in refs:
            mns, mpath = ident(ref)
            if mns != "minecraft" and not model_file(mns, mpath).is_file():
                errors.append(f"{f.relative_to(RES)}: model {mns}:{mpath} -> missing "
                              f"{model_file(mns, mpath).relative_to(RES)}")
    models = ns_dir / "models"
    for f in sorted(models.rglob("*.json")):
        check_model(ns, f.relative_to(models).with_suffix("").as_posix())

errors = list(dict.fromkeys(errors))
if errors:
    print(f"{len(errors)} asset problem(s):")
    for e in errors:
        print("  " + e)
    sys.exit(1)
print("assets OK")
