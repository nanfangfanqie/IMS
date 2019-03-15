package ims.yang.com.ims.adapter;

import android.content.Context;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import ims.yang.com.ims.R;
import ims.yang.com.ims.entity.Message;

import java.util.List;

/**
 * @author yangchen
 * on 3/14/2019 5:46 PM
 */
public class MsgAdapter extends CommonAdapter<Message> {

    public MsgAdapter(Context context, int layoutId, List<Message> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, Message message, int position) {
        holder.setImageResource(R.id.img_user_header, R.drawable.qipao);
        holder.setText(R.id.txt_message, message.getContent());
    }
}
