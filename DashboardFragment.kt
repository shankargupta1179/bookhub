package com.example.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookhub.R
import com.example.bookhub.adapter.DashboardRecyclerAdapter
import com.example.bookhub.model.Book
import com.example.bookhub.util.ConnectionManager
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
       lateinit var recyclerDashboard : RecyclerView

       lateinit var layoutManager:RecyclerView.LayoutManager

       lateinit var progressLayout:RelativeLayout

       lateinit var progressBar:ProgressBar

    lateinit var recyclerAdapter: DashboardRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_dashboard, container, false)

        val bookList= arrayListOf<Book>()

        val bookInfoList= arrayListOf<Book>()

        recyclerDashboard=view.findViewById(R.id.recyclerDashboard)



        progressLayout=view.findViewById(R.id.progressLayout)

        progressBar=view.findViewById(R.id.progressBar)

        progressLayout.visibility=View.VISIBLE

        layoutManager=LinearLayoutManager(activity)



        val queue= Volley.newRequestQueue(activity as Context)

        val url="http://13.235.250.119/v1/book/fetch_books/"

        if(ConnectionManager().checkConnectivity(activity as Context))
        {
            val jsonObjectRequest=object :JsonObjectRequest(Request.Method.GET,url,null, Response.Listener {

                try{
                    progressLayout.visibility=View.GONE
                    val success=it.getBoolean("success")

                    if(success)
                    {

                        val data=it.getJSONArray("data")
                        for(i in 0 until data.length())
                        {
                            val bookJsonObject=data.getJSONObject(i)
                            val bookObject=Book(
                                bookJsonObject.getString("book_id"),
                                bookJsonObject.getString("name"),
                                bookJsonObject.getString("author"),
                                bookJsonObject.getString("rating"),
                                bookJsonObject.getString("price"),
                                bookJsonObject.getString("image")
                            )
                            bookInfoList.add(bookObject)

                            recyclerAdapter= DashboardRecyclerAdapter(activity as Context,bookInfoList)

                            recyclerDashboard.adapter=recyclerAdapter

                            recyclerDashboard.layoutManager=layoutManager

                        }

                    }
                    else{
                        Toast.makeText(activity as Context,"some error occurred!!",Toast.LENGTH_SHORT).show()
                    }
                }catch (e:JSONException)
                {
                    Toast.makeText(activity as Context,"some unexpected error occured!!",Toast.LENGTH_SHORT).show()
                }


            },Response.ErrorListener {

                Toast.makeText(activity as Context,"Volley error occurred",Toast.LENGTH_SHORT).show()
            })
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="0809cbab37676a"
                    return headers
                }

            }

            queue.add(jsonObjectRequest)

        }
        else
        {
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setTitle("error")
            dialog.setMessage("internet connection not found")
            dialog.setPositiveButton("open settings"){
                    text,listener->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("exit"){
                    text,listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.show()
        }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}