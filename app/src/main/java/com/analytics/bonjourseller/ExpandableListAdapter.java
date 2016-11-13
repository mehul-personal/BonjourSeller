package com.analytics.bonjourseller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private ArrayList<String> _listDataHeader;
    private ArrayList<String> _listDataChild;
    public ArrayList<String> _listDirectionDataChild;
    public ArrayList<String> _involvePeople;
    public ArrayList<String> _categoryId;

    // HashMap<String, ArrayList<String>> _listImageDataChild;
    //public ImageLoader imageLoader;

    public ExpandableListAdapter(Context context, ArrayList<String> offername,
                                 ArrayList<String> offerdesc, ArrayList<String> distance,
                                 ArrayList<String> involvePeople, ArrayList<String> category_id) {
        this._context = context;
        this._listDataHeader = offername;
        this._listDataChild = offerdesc;
        this._listDirectionDataChild = distance;
        this._involvePeople = involvePeople;
        this._categoryId = category_id;
        // this._listImageDataChild = listimgchild;
        //imageLoader = new ImageLoader(context.getApplicationContext());
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataHeader.get(groupPosition);
    }

    public Object getDirectionChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(groupPosition);
    }

    public Object getInvolvePeople(int groupPosition, int childPosition) {
        return this._involvePeople.get(groupPosition);
    }

    // public Object getImageChild(int groupPosition, int childPosititon) {
    // return
    // this._listImageDataChild.get(this._listDataHeader.get(groupPosition))
    // .get(childPosititon);
    // }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @SuppressWarnings("unused")
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String offername = (String) getChild(groupPosition, childPosition);
        final String offerdescription = (String) getDirectionChild(
                groupPosition, childPosition);
        final String involvePeople = (String) getInvolvePeople(groupPosition,
                childPosition);
        // final String childImge = (String) getImageChild(groupPosition,
        // childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_expandable_listview, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        TextView txtListDirection = (TextView) convertView
                .findViewById(R.id.txvdetailitemdirection);
        TextView txtInvolvePeople = (TextView) convertView
                .findViewById(R.id.txvdetailInvolvePeople);
        // ImageView img = (ImageView) convertView
        // .findViewById(R.id.imvstoreimage);
        LinearLayout popuplayout = (LinearLayout) convertView.findViewById(R.id.popuplayout);
        //popuplayout.setBackgroundResource(R.drawable.ic_offer_bg);

        txtListChild.setText(offername);
        txtListDirection.setText(Html.fromHtml("" + offerdescription));
        txtInvolvePeople.setText("Offer Involve into " + involvePeople
                + " People");
        // imageLoader.DisplayImage(childImge, img);
        // img.setImageResource(childImge);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    public Object getDistance(int groupPosition) {
        return this._listDirectionDataChild.get(groupPosition);
    }

    public Object getCategory(int groupPosition) {
        return this._categoryId.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        String distance = (String) getDistance(groupPosition);
        String category = (String) getCategory(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_expandable_listview_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        TextView lblListDistance = (TextView) convertView
                .findViewById(R.id.lblListDistance);
        ImageView lblListCategory = (ImageView) convertView.findViewById(R.id.lblListCategory);
        // lblListHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0,
        // R.drawable.i, 0);
        lblListHeader.setText(headerTitle);
        lblListDistance.setText(distance+" KM");

        if (isExpanded) {

            // lblListHeader.setTextColor(Color.parseColor("#fae100"));
            lblListDistance.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.ic_up_arrow, 0);
            lblListDistance.setCompoundDrawablePadding(10);
            //convertView.setBackgroundColor(Color.parseColor("#fae100"));

        } else {

            //convertView.setBackgroundColor(Color.parseColor("#ffffff"));
            // lblListHeader.setTextColor(Color.parseColor("#000000"));
            lblListDistance.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.ic_down_arrow, 0);
            lblListDistance.setCompoundDrawablePadding(10);
        }
        if (category.equalsIgnoreCase("1")) {
            lblListCategory.setImageResource(R.drawable.ic_category_restarent);
        } else if (category.equalsIgnoreCase("3")) {
            lblListCategory.setImageResource(R.drawable.ic_category_merchant);
        } else if (category.equalsIgnoreCase("5")) {
            lblListCategory.setImageResource(R.drawable.ic_category_real_estate);
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
