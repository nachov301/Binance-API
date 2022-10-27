package app.iggy.binancechallenge.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import app.iggy.binancechallenge.Common.Common
import app.iggy.binancechallenge.R
import app.iggy.binancechallenge.network.websocketModel.BidsAndAsks
import app.iggy.binancechallenge.viewModel.MainActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.Viewport
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.util.*


class GraphFragment : BottomSheetDialogFragment() {

    lateinit var graph: GraphView
    private var viewport: Viewport? = null
    private var pointsPlotted = 5
    lateinit var txtBidsAndAsks: TextView
    lateinit var txtSymbol: TextView

    private var ask = 0.0
    private var bid = 0.0

    var series = LineGraphSeries(arrayOf<DataPoint>())

    private var symbol = ""

    private lateinit var viewModelRoutesFragment : MainActivityViewModel


    private val TAG = "GraphFragment"

    companion object {
        private val instance: GraphFragment? = null
        fun getInstance(): GraphFragment? {
            return instance ?: GraphFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_graph, container, false)

        init(view)

        initGraph()

        viewModelRoutesFragment =
            ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]

        symbol = arguments?.getString(Common.SYMBOL).toString()

        txtSymbol.text = symbol.uppercase(Locale.getDefault())

        viewModelRoutesFragment.subscribeToWebSocket(symbol)

        viewModelRoutesFragment.webSocketMessage.observe(viewLifecycleOwner) { webSocketMessage ->
            drawGraph(webSocketMessage)
        }

        return view
    }

    private fun drawGraph(message: String?) {

        var x: Double
        x = -5.0

        message?.let {
            val moshi = Moshi.Builder().build()
            val adapter: JsonAdapter<BidsAndAsks> = moshi.adapter(BidsAndAsks::class.java)
            val crypto = adapter.fromJson(it)
            activity?.runOnUiThread {

                if (crypto?.asks != null && crypto.bids != null) {

                    ask = crypto.asks[0][0].toDouble()
                    bid = crypto.bids[0][0].toDouble()

                    txtBidsAndAsks.text = "Ask: $ask \nBid: $bid"

                    x = ask

                    //upgrade the graph  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

                    pointsPlotted++
                    series.appendData(
                        DataPoint(pointsPlotted.toDouble(), x),
                        true, pointsPlotted
                    )

                    viewport!!.setMaxX(pointsPlotted.toDouble())
                    viewport!!.setMinX((pointsPlotted - 200).toDouble())

                    if (pointsPlotted > 1000) {
                        pointsPlotted = 1
                        series.resetData(arrayOf(DataPoint(pointsPlotted.toDouble(), 0.0)))
                    }

                    //upgrade the graph  +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModelRoutesFragment.closeWebSocket()
    }

    private fun initGraph() {
        viewport = graph.viewport
        if (viewport != null) {
            viewport!!.isScrollable = true
            viewport!!.isXAxisBoundsManual = true
            viewport!!.isYAxisBoundsManual = false
        }
        graph.addSeries(series)
    }

    private fun init(view: View) {
        txtBidsAndAsks = view.findViewById(R.id.txt_bids_and_asks)
        txtSymbol = view.findViewById(R.id.txt_symbol)
        graph = view.findViewById(R.id.graph)
    }
}