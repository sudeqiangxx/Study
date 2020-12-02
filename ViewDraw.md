upspecified :父容器对于子容器没有任何限制，子容器想要多大就多大
exactly ：父容器已经为子容器设置了尺寸，子容器应当服从这些边界，不论子容器想要多大的空间。
at_most: 子容器可以声明大小内的任意大小。
父view 会先测量子view ：measureChildWithMargins ，然后在测量自己