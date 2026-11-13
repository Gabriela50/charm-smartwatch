import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

class CharmStructureTests(unittest.TestCase):

    def test_android_manifest_exists(self):
        self.assertTrue(
            any(ROOT.glob("**/src/main/AndroidManifest.xml")),
            "No se encontro AndroidManifest.xml"
        )

    def test_kotlin_sources_exist(self):
        self.assertTrue(
            any(ROOT.glob("**/src/main/**/*.kt")),
            "No se encontraron fuentes Kotlin"
        )

    def test_gradle_configuration_exists(self):
        self.assertTrue(
            any(ROOT.glob("**/build.gradle.kts")) or
            any(ROOT.glob("**/build.gradle")),
            "No se encontro configuracion Gradle"
        )

    def test_manifest_declares_application(self):
        manifests = list(ROOT.glob("**/src/main/AndroidManifest.xml"))
        self.assertTrue(manifests)
        self.assertTrue(
            any("<application" in p.read_text() for p in manifests)
        )

    def test_kotlin_sources_are_nonempty(self):
        sources = list(ROOT.glob("**/src/main/**/*.kt"))
        self.assertTrue(sources)
        self.assertTrue(all(p.stat().st_size > 0 for p in sources))

    def test_project_has_readme(self):
        self.assertTrue(
            any(ROOT.glob("README*")),
            "El proyecto necesita un README"
        )

if __name__ == "__main__":
    unittest.main()
