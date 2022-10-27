package app.iggy.binancechallenge.view

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.iggy.binancechallenge.Common.Common
import app.iggy.binancechallenge.R
import app.iggy.binancechallenge.adapter.BinanceAdapter
import app.iggy.binancechallenge.fragments.GraphFragment
import app.iggy.binancechallenge.interfaces.AdapterCallBack
import app.iggy.binancechallenge.network.listModel.JsonClassListModelItem
import app.iggy.binancechallenge.network.websocketModel.BidsAndAsks
import app.iggy.binancechallenge.viewModel.MainActivityViewModel
import com.airbnb.lottie.LottieAnimationView
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi


class MainActivity : AppCompatActivity(), AdapterCallBack {

    private val TAG = "MainActivity"

    private lateinit var viewModel: MainActivityViewModel

    lateinit var binanceAdapter: BinanceAdapter
    lateinit var binanceRV: RecyclerView

    lateinit var lottieAnim: LottieAnimationView

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var flagLoading = false

    private val list: ArrayList<JsonClassListModelItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        init()

        //calls retrofit to get the list
        viewModel.getInfo()
        showLoadingAnim()

        viewModel.showProgress.observe(this) { loading ->
            if (flagLoading) {
                swipeRefreshLayout.isRefreshing = loading
            }
            if (!flagLoading && !loading) {
                hideAnim()
                flagLoading = true
            }
        }

        viewModel.response.observe(this) { jsonList ->
            list.clear()
            for (i in 0 until jsonList.size) {
                list.add(jsonList[i])
            }
            binanceRV = findViewById(R.id.recycler_binance)
            binanceRV.layoutManager = LinearLayoutManager(this@MainActivity)
            binanceAdapter = BinanceAdapter(list, this as AdapterCallBack)
            binanceRV.adapter = binanceAdapter
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.getInfo()
        }


    }

    private fun init() {
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        lottieAnim = findViewById(R.id.lottie_loading)
        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val search = menu.findItem(R.id.actionSearch)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search crypto..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }


    private fun filter(text: String) {

        val filteredlist: ArrayList<JsonClassListModelItem> = arrayListOf()

        for (item in list) {
            if (item.symbol.lowercase().contains(text.lowercase())
            ) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            showEmptyAnim()
        } else {
            hideAnim()
        }
        binanceAdapter.filterList(filteredlist)
    }

    private fun showEmptyAnim() {
        lottieAnim.setAnimation(R.raw.empty_anim)
        if (lottieAnim.visibility != View.VISIBLE) {
            lottieAnim.visibility = View.VISIBLE
        }
    }

    private fun showLoadingAnim() {
        lottieAnim.setAnimation(R.raw.loading_animation)
        if (lottieAnim.visibility != View.VISIBLE) {
            lottieAnim.visibility = View.VISIBLE
        }
    }

    private fun hideAnim() {
        if (lottieAnim.visibility != View.GONE) {
            lottieAnim.visibility = View.GONE
        }
    }

    override fun callback(symbol: String) {
        val fragment: GraphFragment? = GraphFragment.getInstance()
        val bundle = Bundle()
        bundle.putString(Common.SYMBOL, symbol)
        fragment?.arguments = bundle
        fragment?.show(supportFragmentManager, "Graph")
    }


}