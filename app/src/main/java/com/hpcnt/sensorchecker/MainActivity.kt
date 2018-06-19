package com.hpcnt.sensorchecker

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.hpcnt.sensorchecker.camera.CameraExtensions
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    var currentView: BaseView? = null

    private val itemAdapter by lazy { ItemAdapter<ContentItem>() }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        currentView?.output?.removeObservers(this)
        currentView?.release()
        itemAdapter.clear()

        fun update() {
            currentView?.output?.observe(this, Observer<List<ContentItem>> { list: List<ContentItem>? ->
                list ?: return@Observer
                itemAdapter.add(list)
            })

            currentView?.trigger()
        }

        when (item.itemId) {
            R.id.navigation_back_camera -> {
                currentView = CameraView(applicationContext, CameraExtensions.FACING.BACK)
                update()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_front_camera -> {
                currentView = CameraView(applicationContext, CameraExtensions.FACING.FRONT)
                update()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_sensors -> {
                currentView = SensorView(applicationContext)
                update()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fastAdapter = FastAdapter.with<ContentItem, ItemAdapter<ContentItem>>(itemAdapter)

        contents_view.layoutManager = LinearLayoutManager(this)
        contents_view.itemAnimator = DefaultItemAnimator()
        contents_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        contents_view.adapter = fastAdapter


        navigation_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        TedPermission.with(this)
                .setPermissionListener (object : PermissionListener {
                    override fun onPermissionGranted() {
                        navigation_view.findViewById<View>(R.id.navigation_back_camera).performClick()
                    }

                    override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                        finish()
                    }
                })
                .setPermissions(android.Manifest.permission.CAMERA)
                .check()
    }
}

class ContentItem(val name: String, val description: String) : AbstractItem<ContentItem, ContentItem.ViewHolder>() {
    override fun getType(): Int = 0

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.view_content

    inner class ViewHolder(view: View) : FastAdapter.ViewHolder<ContentItem>(view) {

        private val headerView: TextView = view.findViewById(R.id.header_view)
        private val bodyView: TextView = view.findViewById(R.id.body_view)

        override fun bindView(item: ContentItem?, payloads: MutableList<Any>?) {
            headerView.text = item?.name
            bodyView.text = item?.description
        }

        override fun unbindView(item: ContentItem?) {
            headerView.text = null
            bodyView.text = null
        }

    }
}