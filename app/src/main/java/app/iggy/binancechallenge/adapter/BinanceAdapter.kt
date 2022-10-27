package app.iggy.binancechallenge.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.iggy.binancechallenge.R
import app.iggy.binancechallenge.fragments.GraphFragment
import app.iggy.binancechallenge.interfaces.AdapterCallBack
import app.iggy.binancechallenge.network.listModel.JsonClassListModelItem
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class BinanceAdapter(var binanceList: ArrayList<JsonClassListModelItem>,
                     private val adapterCallBack: AdapterCallBack)
    : RecyclerView.Adapter<BinanceAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val crypto_symbol = itemView.findViewById<TextView>(R.id.crypto_symbol)
        val crypto_name = itemView.findViewById<TextView>(R.id.crypto_name)
        val crypto_price = itemView.findViewById<TextView>(R.id.crypto_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.binance_item,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (binanceList.isNotEmpty()){
            holder.crypto_symbol.text = binanceList[position].symbol
            holder.crypto_name.text = ""

            val roundOffPrice = roundOffDecimal(binanceList[position].bidPrice.toDouble())
            holder.crypto_price.text = "ARS $roundOffPrice" //Round it off to argentinian pesos

            holder.itemView.setOnClickListener {

                adapterCallBack.callback(binanceList[position].symbol.lowercase(Locale.getDefault()))

            }
        }
    }

    private fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.####")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    override fun getItemCount(): Int {
        return binanceList.size
    }

    fun filterList(filterlist: ArrayList<JsonClassListModelItem>) {
        binanceList = filterlist
        notifyDataSetChanged()
    }
}