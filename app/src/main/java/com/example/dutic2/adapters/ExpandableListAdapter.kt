package com.example.dutic2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.example.dutic2.R

class ExpandableListAdapter(
    var context: Context,
    var listTitles: MutableList<String>,
    var listItems: MutableList<MutableList<String>>
) : BaseExpandableListAdapter() {
    override fun getGroup(groupPosition: Int): Any {
        return listTitles[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        val title = getGroup(groupPosition) as String
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expandable_list_group, null)
        }
        convertView?.findViewById<TextView>(R.id.expandableListTitle)?.text = title
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return listItems.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return listItems[groupPosition][childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        val title = getChild(groupPosition, childPosition) as String
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.exapandable_list_item, null)
        }
        convertView?.findViewById<TextView>(R.id.expandableListItem)?.text = title
        return convertView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return listTitles.size
    }
}