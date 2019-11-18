package com.example.dutic2.adapters

import android.app.Activity
import android.net.Uri
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.example.dutic2.R
import java.io.File

class ViewPagerFotosAdpater(var activity: Activity, var paths: ArrayList<File>) : PagerAdapter() {

    lateinit var imageView: SubsamplingScaleImageView

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return paths.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
            .inflate(R.layout.view_pager_item, container, false)
        val dis = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dis)
        imageView = view.findViewById(R.id.view_pager_image)
        val height = dis.heightPixels
        val width = dis.widthPixels
        imageView.minimumHeight = height
        imageView.minimumWidth = width
        try {
            val a = ImageSource.uri(Uri.fromFile(paths[position]))
            imageView.setImage(a,imageView.state)
//            GlideApp.with(view).load(paths[position]).into(imageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}