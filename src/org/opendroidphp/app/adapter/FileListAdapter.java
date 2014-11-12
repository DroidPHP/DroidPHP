package org.opendroidphp.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.opendroidphp.R;
import org.opendroidphp.app.common.parser.Finder;

import java.util.HashMap;

public class FileListAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private HashMap<Integer, Finder> items;

    public FileListAdapter(Context context, final HashMap<Integer, Finder> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Finder getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.file_list_item, null);
        }
        Finder finder = getItem(position);
        String fileName = finder.find("file_name");
        String bindName = String.format("%s:%s", finder.getAddress(), finder.getPort());

        ((TextView) convertView.findViewById(R.id.txt_name)).
                setText(fileName);
        ((TextView) convertView.findViewById(R.id.txt_info)).
                setText(bindName);
        convertView.findViewById(R.id.file_ll).setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View view) {
        //AppController.toast(context, "Clicked ");
    }
}