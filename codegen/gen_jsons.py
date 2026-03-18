import os, json, yaml


if 'paths':
    ROOT_RESOURCES = '../common/src/main/resources'
    ROOT_FABRIC = '../fabric/src/main/resources'
    ROOT_FORGE = '../forge/src/main/resources'

    # assets
    ROOT_ASSETS = f'{ROOT_RESOURCES}/assets/hexautomata'
    DIR_MODELS = f'{ROOT_ASSETS}/models/item/reactive_focus'

    # data
    ROOT_DATA = f'{ROOT_RESOURCES}/data/hexautomata'
    DIR_RECIPES = f'{ROOT_DATA}/recipes/brainsweep/reactive_focus'

    # resloc
    ID_PRE = 'hexautomata:item/reactive_focus'

if 'helpers':

    class ModelBuilder:
        def __init__(self, parent="item/generated"):
            self.data = {}
            self.parent(parent)

        def parent(self, parent):
            self.data['parent'] = parent
            return self

        def textures(self, layers):
            textures = self.data['textures'] = self.data.get('textures', {})
            for i, tex in enumerate(layers):
                if not tex:
                    continue
                textures[f'layer{i}'] = tex
            return self

        def overrides(self, overrides):
            self.data['overrides'] = overrides
            return self

    def dump_json(path, obj):
        os.makedirs(os.path.dirname(path), exist_ok=1)
        with open(path, 'w', encoding='utf-8') as f:
            json.dump(obj, f, indent=2)

    def dump_brainsweep(
        id, entity, cost=1000000, src_block='hexcasting:akashic_record'
    ):
        obj = {
            "type": "hexcasting:brainsweep",
            "blockIn": {"type": "block", "block": src_block},
            "cost": cost,
            "entityIn": {"type": "entity_type", "entityType": entity},
            "result": {"name": f"hexautomata:reactive_focus/{id}"},
        }
        dump_json(f'{DIR_RECIPES}/{id}.json', obj)

    def dump_models(id, color):
        dir_base = f'{DIR_MODELS}/{id}.json'
        builder = ModelBuilder(f'{ID_PRE}/idle/{id}').overrides(
            [
                {
                    "model": f"{ID_PRE}/active/{id}",
                    "predicate": {"hexautomata:data": 1.0},
                }
            ]
        )
        dump_json(f'{DIR_MODELS}/{id}.json', builder.data)

        for mode in 'active', 'idle':
            builder = ModelBuilder(f'{ID_PRE}/_{mode}').textures(
                [f'hexautomata:item/reactive_focus/inner/{color}']
            )
            dump_json(f'{DIR_MODELS}/{mode}/{id}.json', builder.data)

    def dump_tags(data):
        ids = ['hexautomata:reactive_focus/' + e['id'] for e in data]
        if 'curios/trinkets':
            obj = {"replace": False, "values": ids}
            dump_json(f'{ROOT_FABRIC}/data/trinkets/tags/items/all.json', obj)
            dump_json(f'{ROOT_FORGE}/data/curios/tags/items/curio.json', obj)


with open('data.yaml') as f:
    data = yaml.load(f, yaml.Loader)

for event in data:
    dump_brainsweep(event['id'], event['entity'])
    dump_models(event['id'], event['color'])

dump_tags(data)
