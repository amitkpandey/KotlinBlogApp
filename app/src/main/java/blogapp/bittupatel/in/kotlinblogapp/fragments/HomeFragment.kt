package blogapp.bittupatel.`in`.kotlinblogapp.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AbsListView
import blogapp.bittupatel.`in`.kotlinblogapp.Post
import blogapp.bittupatel.`in`.kotlinblogapp.R
import blogapp.bittupatel.`in`.kotlinblogapp.adapters.HomeAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.loadmore_refresh.*
import kotlinx.android.synthetic.main.progress_bar.*
import org.json.JSONException
import org.json.JSONObject


class HomeFragment : Fragment() {


    private var homeRC: RecyclerView? = null
    var posts: MutableList<Post?> = ArrayList()
    private lateinit var adapter: HomeAdapter
    var isScrolling: Boolean? = false
    var currentItems: Int = 0
    var totalItems: Int = 0
    var scrollOutItems: Int = 0
    var pageNumber: Int = 1
    lateinit var manager: LinearLayoutManager
    internal var id: Int = 0
    private lateinit var title_plain: String
    private lateinit var content: String
    private lateinit var date: String
    private lateinit var author: String
    private lateinit var thumbnail: String



    companion object {
        fun newInstance(): HomeFragment {
            val fragmentHome = HomeFragment()
            return fragmentHome
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_home, container, false)
        homeRC = rootView.findViewById<View>(R.id.recyclerView_home) as RecyclerView
        manager = LinearLayoutManager(HomeFragment.newInstance().context)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = HomeAdapter(posts)
        homeRC?.layoutManager = manager
        homeRC?.adapter = adapter

        homeRC?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItems = manager.childCount
                totalItems = manager.itemCount
                scrollOutItems = manager.findFirstVisibleItemPosition()

                if (isScrolling!! && (currentItems + scrollOutItems === totalItems)) {
                    isScrolling = false
                    loadData()
                    loadMore.visibility = VISIBLE
                }
            }
        })
        loadData()
    }

    private fun loadData() {
        val URL_DATA = "http://www.thetechsamachar.com/api/get_posts?page=$pageNumber"
        val stringRequest = StringRequest(Request.Method.GET, URL_DATA,
                Response.Listener { s ->
                    try {
                        val jsonObject = JSONObject(s)
                        val array = jsonObject.getJSONArray("posts")
                        for (i in 0 until array.length()) {
                            val ob = array.getJSONObject(i)
                            id = ob.getInt("id")
                            title_plain = ob.getString("title_plain")
                            content = ob.getString("content")
                            date = ob.getString("date")
                            author = ob.getJSONObject("author").getString("name")
                            thumbnail = try {
                                ob.getJSONArray("attachments").getJSONObject(0).getString("url")
                            } catch (e: JSONException) {
                                "http://www.thetechsamachar.com/wp-content/uploads/2017/12/IMG-20170821-WA0001.jpg"
                            }
                            println(thumbnail)
                            val postItem = Post(id, title_plain, content, date, author, thumbnail)
                            posts.add(postItem)
                        }
                        pageNumber += 1
                        adapter.notifyDataSetChanged()
                        progessBar.visibility = GONE
                        loadMore.visibility = GONE
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { })
        val requestQueue = Volley.newRequestQueue(activity)
        requestQueue.add(stringRequest)
    }

}





