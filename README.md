# Android-YRichEditor

an android richedtor with native implementation

![](http://7ktocj.com1.z0.glb.clouddn.com/device-2017-05-25-163507.png?imageView2/0/w/500)


# Feature

1. 支持图文并排。
2. 支持加粗和链接。
3. 支持本地图片和网络图片。

# Output

1. 图片，标题，段落作为一级标签。
2. 加粗，链接作为段落的内嵌标签。

示例：

```html

<h1>这是一级标题</h1>
<p>段落1<a href="http://heiman.com">hei man</a></p>
<p>段落2<b>我是加粗部分</b>hello<br></p>
<img src="http://github.com/pic.png" width="100", height="100"/>
<p>段落3<b>加粗加粗加粗<br>加粗加粗加粗</b></p>

```

# More

详细使用请查看示例，有关原理可以查看[Android原生简易图文编辑器](https://yedaxia.github.io/Android-RichEditor-And-NativeHtml/)

**Thanks:**

- XRichText: https://github.com/sendtion/XRichText
- cwac-richedit: https://github.com/commonsguy/cwac-richedit
- Html解析库Jsoup：https://jsoup.org/
