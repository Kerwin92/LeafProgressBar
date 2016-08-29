# LeafProgressBar
##参考
[一个绚丽的loading动效分析与实现！](http://blog.csdn.net/tianjian4592/article/details/44538605)
项目效果图：
![](http://img.blog.csdn.net/20150322173842391?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdGlhbmppYW40NTky/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

作者提供的代码的问题：
1. 背景图和风扇分别以图片的形式添加，屏幕适配存在问题
2. 结构和流程完整，但是不少细节未处理

本项目在“肖天师”的修改上再做修改，[“肖天师”代码](https://github.com/cpxiao/LeafProgressBar)
##本人已完成
1. 代码中加入了本人自己的一些理解注释，与原文作者提供的代码有几处稍有改动
2. 增加了对wrap_content的支持
3. 增加了对padding的支持
4. 加载完成后的处理动画

##待完成
1. 转动速度、叶子数量与加载速率的关联
2. 叶子到达终点位置时，有时候会微露
