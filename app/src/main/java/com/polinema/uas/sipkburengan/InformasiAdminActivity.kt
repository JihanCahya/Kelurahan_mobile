package com.polinema.uas.sipkburengan

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.polinema.uas.sipkburengan.databinding.ActivityInformasiAdminBinding

class InformasiAdminActivity : Fragment() {

    lateinit var thisParent: DashboardAdminActivity
    private lateinit var b : ActivityInformasiAdminBinding
    lateinit var v: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as DashboardAdminActivity
        b = ActivityInformasiAdminBinding.inflate(layoutInflater)
        v = b.root

        return v
    }
}