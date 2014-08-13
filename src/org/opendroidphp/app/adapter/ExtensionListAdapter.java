package org.opendroidphp.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.opendroidphp.R;
import org.opendroidphp.app.model.ExtensionItem;

import java.util.ArrayList;

public class ExtensionListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ExtensionItem> extensionItems;

    public ExtensionListAdapter(Context context, ArrayList<ExtensionItem> extensionItems) {
        this.context = context;
        this.extensionItems = extensionItems;
    }

    @Override
    public int getCount() {
        return extensionItems.size();
    }

    @Override
    public Object getItem(int position) {
        return extensionItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.extensions_list_item, null);
        }

        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        txtTitle.setText(extensionItems.get(position).getName());

        return convertView;
    }

}

