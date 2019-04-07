package top.eatfingersss.sudokugamedemo.model.entity;


public class BlocksColor{
    public BlockColor[] defaultBlock;//默认块 需要分颜色 基本上就是两个
    public BlockColor selectBlock;//高亮/选中块
    public BlockColor givenBlock;//固定块
    public BlockColor repeatedBlock;//错误块
    public BlockColor ableBlock;//不可编辑（同步）块

    public BlocksColor(BlockColor[] defaultBlock,
                       BlockColor selectBlock,
                       BlockColor givenBlock,
                       BlockColor repeatedBlock,
                       BlockColor ableBlock) {

        this.defaultBlock = defaultBlock;
        this.selectBlock = selectBlock;
        this.givenBlock = givenBlock;
        this.repeatedBlock = repeatedBlock;
        this.ableBlock = ableBlock;
    }
}
//    int lightBlockColor;//高亮/选中块
//    int defaultBlockColor;//默认
//    int textColor;//字体颜色
//    int hardColor;//固定/给出块
//    int