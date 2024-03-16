package com.example.petmatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class activity_navegation : AppCompatActivity() {

    private lateinit var navegador:BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navegation)

        replaceFragment(FirstFragment())

        /*navegador.setOnItemSelectedListener {
            when(it.itemId){
                R.id.action_search -> replaceFragment(FirstFragment())
                R.id.action_navigation -> replaceFragment(FirstFragment())
                else ->{
                }
            }
            true
        }*/
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}