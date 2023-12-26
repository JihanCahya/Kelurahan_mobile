package com.polinema.uas.sipkburengan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.polinema.uas.sipkburengan.databinding.ActivityHomePenggunaBinding

class HomePenggunaActivity : Fragment() {
    private lateinit var b : ActivityHomePenggunaBinding
    lateinit var thisParent: MainActivity
    lateinit var v: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisParent = activity as MainActivity
        b = ActivityHomePenggunaBinding.inflate(layoutInflater)
        v = b.root

        return v
    }
}