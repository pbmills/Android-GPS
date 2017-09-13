package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rush.rushdigital.R;

import java.util.ArrayList;

import Bean.MainActivityVo;

/**
 * Created by zithas on 16/8/17.
 */

public class MainActivityAdapter extends BaseAdapter {

    private Context context;
    private MainActivityVo mainActivityVo = new MainActivityVo();
    private ArrayList<MainActivityVo> arrayList = new ArrayList<MainActivityVo>();
    private TextView tv_time,tv_address;

    public MainActivityAdapter(Context context, ArrayList<MainActivityVo> arrayList1) {
        this.context = context;
        this.arrayList = arrayList1;

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.raw_list_view, null);
        }
        mainActivityVo = new MainActivityVo();
        mainActivityVo = arrayList.get(position);

            tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            tv_time.setText(mainActivityVo.getTime());
            tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            tv_address.setText(mainActivityVo.getAddress());

        return convertView;

    }
}
