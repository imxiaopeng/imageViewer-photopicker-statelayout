图片浏览框架，可左右滑动，双指放大图片，单击图片返回
imageviewer使用：
    Intent intent=new Intent(mContext,ImagePreviewActivity.class);
    intent.putExtra("list",mList);//数据源
    intent.putExtra("position",mPosition);//默认展示第几个位置
    mContext.startActivity(intent);