from pathlib import Path
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
manifest = root / "app/src/main/AndroidManifest.xml"
assert manifest.is_file(), "No existe AndroidManifest.xml"
tree = ET.parse(str(manifest))
assert tree.getroot().tag == "manifest"
assert tree.find("application") is not None

sources = list((root / "app/src/main").rglob("*.kt"))
assert sources, "No existen fuentes Kotlin"
assert all(p.stat().st_size > 0 for p in sources)

print("OK: manifiesto Android válido y fuentes Kotlin presentes")
