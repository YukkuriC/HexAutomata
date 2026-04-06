from PIL import Image
import os
import shutil


def hue_shift(image, shift_degrees):
    """
    Credits: DeepSeek
    对输入图片应用色相偏移，返回新图片（RGBA模式）。

    Args:
        image: PIL Image对象，推荐为RGBA模式（若不含Alpha会自动添加）
        shift_degrees: 色相偏移角度（0~360），支持负值或超过360的值

    Returns:
        PIL Image对象（RGBA），色相已偏移
    """
    # 确保图片为RGBA模式，以便保留透明度
    image = image.convert('RGBA')
    r, g, b, a = image.split()

    # 合并RGB部分并转换为HSV
    rgb = Image.merge('RGB', (r, g, b))
    hsv = rgb.convert('HSV')
    h, s, v = hsv.split()

    # 将角度偏移映射到HSV的H通道范围（0~255）
    # 注意：PIL中HSV的H范围是0~255，对应0~360°
    shift = int((shift_degrees % 360) / 360.0 * 255)

    # 对H通道进行偏移（模255）
    h = h.point(lambda x: (x + shift) % 255)

    # 合并HSV并转回RGB
    hsv_shifted = Image.merge('HSV', (h, s, v))
    rgb_shifted = hsv_shifted.convert('RGB')

    # 重新组合Alpha通道
    r_shifted, g_shifted, b_shifted = rgb_shifted.split()
    rgba_shifted = Image.merge('RGBA', (r_shifted, g_shifted, b_shifted, a))

    return rgba_shifted


if 'data':
    ROOT_DIR = os.path.abspath(
        os.path.join(
            __file__, '../..', 'common/src/main/resources/assets/hexautomata/textures'
        )
    )
    BASE_COLOR = 'red'
    HUE_MAP = {
        'orange': 30,
        'yellow': 60,
        'green': 120,
        'cyan': 180,
        'blue': -120,
        'purple': -90,
        # 'magenta': -60,
    }

    def process_group(subpath):
        folder = os.path.join(ROOT_DIR, subpath)
        tofile = lambda color: os.path.join(folder, color + '.png')
        base_img_dir = tofile(BASE_COLOR)
        base_img = Image.open(base_img_dir)
        base_meta_dir = base_img_dir + '.mcmeta'
        has_meta = os.path.isfile(base_meta_dir)

        for color, shift in HUE_MAP.items():
            shifted = hue_shift(base_img, shift)
            output_dir = tofile(color)
            shifted.save(output_dir)
            print('Output:', os.path.relpath(output_dir, ROOT_DIR))

            if has_meta:
                shutil.copyfile(base_meta_dir, output_dir + '.mcmeta')


process_group('item/reactive_focus/inner_idle')
process_group('item/reactive_focus/inner_active')
