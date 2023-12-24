package com.polinema.uas.sipkburengan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.polinema.uas.sipkburengan.databinding.ActivityKritikSaranAdminBinding

class KritikSaranAdminActivity : Fragment() {
    lateinit var thisParent: DashboardAdminActivity
    private lateinit var b: ActivityKritikSaranAdminBinding
    lateinit var v: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as DashboardAdminActivity
        b = ActivityKritikSaranAdminBinding.inflate(layoutInflater)
        v = b.root

        return v
    }
}