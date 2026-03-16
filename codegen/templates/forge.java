package io.yukkuric.hexautomata.forge;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import static io.yukkuric.hexautomata.HAConfig.*;

{% set data_common = filter_val.side.common -%}
{% set data_forge = filter_val.side.forge -%}
public class HAConfigForge implements API {
    public static HAConfigForge INSTANCE;
    {%- for line in data_forge %}
    private static final String desc_{{line.name}} = "{{line.descrip}}";
    public static {{line.type}} {{line.name}}() {
        return INSTANCE.cfg_{{line.name}}.get();
    }
    {%- endfor %}{{'\n'}}

    {%- for line in data_common %}
    public {{line.type}} {{line.name}}() {
        return cfg_{{line.name}}.get();
    }
    {%- endfor %}{{'\n'}}

    {%- for grp,lines in group_val(data,'type') %}
    public ForgeConfigSpec.{{grp.capitalize()}}Value{% for line in lines %}
            cfg_{{line.name}}
            {%- if loop.last %};{% else %},{% endif %}
        {%- endfor %}
    {%- endfor %}

    public HAConfigForge(ForgeConfigSpec.Builder builder) {
        {%- for grp,lines in group_val(data,'category') %}
        {%- set grp_pieces = grp.split('.') -%}
        {%- for piece in grp_pieces %}
        builder.push("{{piece}}");
        {%- endfor %}
        {%- for line in lines %}
        cfg_{{line.name}} = builder.comment(desc_{{line.name}}) {{-''-}}
                .define{% if line.type == 'boolean' %}{% else %}InRange{% endif %}("{{line.name}}", {{line.default}});
        {%- endfor %}
        {%- for piece in grp_pieces %}
        builder.pop();
        {%- endfor %}{{'\n'}}
        {%- endfor %}
        INSTANCE = this;
    }

    private static final Pair<HAConfigForge, ForgeConfigSpec> CFG_REGISTRY;

    static {
        CFG_REGISTRY = new ForgeConfigSpec.Builder().configure(HAConfigForge::new);
    }

    public static void register(ModLoadingContext ctx) {
        bindConfigImp(CFG_REGISTRY.getKey());
        ctx.registerConfig(ModConfig.Type.COMMON, CFG_REGISTRY.getValue());
    }
}