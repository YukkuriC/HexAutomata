from importlib.resources import Package
from typing_extensions import override

from hexdoc.plugin import (
    HookReturn,
    ModPlugin,
    ModPluginImpl,
    ModPluginWithBook,
    hookimpl,
)

import hexdoc_hexautomata

from .__gradle_version__ import FULL_VERSION, GRADLE_VERSION
from .__version__ import PY_VERSION


class HexAutomataPlugin(ModPluginImpl):
    @staticmethod
    @hookimpl
    def hexdoc_mod_plugin(branch: str) -> ModPlugin:
        # monkey-patch item texture
        def add_texture1(func):
            def inner(self, loader, gaslighting_items, checked_overrides=None):
                res = func(self, loader, gaslighting_items, checked_overrides)
                if not res:
                    layer1 = None
                    if self.textures:
                        layer1 = self.textures.get("layer1")
                    if layer1:
                        texture_id = "textures" / layer1 + ".png"
                        return "texture", texture_id
                return res

            return inner

        from hexdoc.minecraft.assets.models import ModelItem

        ModelItem.find_texture = add_texture1(ModelItem.find_texture)

        return HexAutomataModPlugin(branch=branch)


class HexAutomataModPlugin(ModPluginWithBook):
    @property
    @override
    def modid(self) -> str:
        return "hexautomata"

    @property
    @override
    def full_version(self) -> str:
        return FULL_VERSION

    @property
    @override
    def mod_version(self) -> str:
        return GRADLE_VERSION

    @property
    @override
    def plugin_version(self) -> str:
        return PY_VERSION

    @override
    def resource_dirs(self) -> HookReturn[Package]:
        # lazy import because generated may not exist when this file is loaded
        # eg. when generating the contents of generated
        # so we only want to import it if we actually need it
        from ._export import generated

        return generated

    @override
    def jinja_template_root(self) -> tuple[Package, str]:
        return hexdoc_hexautomata, "_templates"
