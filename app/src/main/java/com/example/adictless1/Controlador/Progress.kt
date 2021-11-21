package com.example.adictless1.Controlador

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.adictless1.Login
import com.example.adictless1.R
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_progress.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [Progress.newInstance] factory method to
 * create an instance of this fragment.
 */
class Progress : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity: Login? = activity as Login?
        val usuario: CharSequence? = activity?.usuario()

        val addStatCard = view?.findViewById<CardView>(R.id.addHoursCardView)
        addStatCard?.visibility = View.GONE

        val login_usuario = view?.findViewById<TextView>(R.id.textView6)
        login_usuario?.text = usuario
        setBarChart()

        val addButton = view?.findViewById<Button>(R.id.addStatButton)
        addButton?.setOnClickListener(){
            if(addStatCard?.visibility == View.GONE){
                addStatCard?.visibility = View.VISIBLE
            }
            else{
                addStatCard?.visibility = View.GONE
                //Añadir estadistica()
            }
        }

        val addConfirmButton = view?.findViewById<Button>(R.id.addStatButton2)
        addConfirmButton?.setOnClickListener(){
            val values = Array<Float>(7){ Math.random().toFloat() }

        }
    }

    private fun setBarChart(values : Array<Float> ){
        val entries = ArrayList<BarEntry>()
        //val values = Array<Float>(7){ Math.random().toFloat() }
        /* Inicializar datos de values con los de la base de datos*/

        for(value in values){
            entries.add(BarEntry(value, values.indexOf(value)))
        }

        /*entries.add(BarEntry(8f, 0))
        entries.add(BarEntry(2f, 1))
        entries.add(BarEntry(5f, 2))
        entries.add(BarEntry(20f, 3))
        entries.add(BarEntry(15f, 4))
        entries.add(BarEntry(19f, 5))
        entries.add(BarEntry(5f, 6))
         */

        val barDataSet = BarDataSet(entries, "Horas")

        val labels = ArrayList<String>()
        labels.add("Lun")
        labels.add("Mar")
        labels.add("Mie")
        labels.add("Jue")
        labels.add("Vie")
        labels.add("Sab")
        labels.add("Dom")
        val data = BarData(labels, barDataSet)
        barChart.data = data // set the data and list of lables into chart

        barChart.setDescription("Tiempo de uso semanal")  // set the description

        //barDataSet.setColors(ColorTemplate.COLORFUL_COLORS)
        barDataSet.color = resources.getColor(R.color.blueProgress)

        barChart.animateY(5000)
    }
}