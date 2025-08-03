package com.example.musicapp

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

//private lateinit var  musiclist1 : ArrayList<Music>
//private lateinit var  musiclist2 : ArrayList<Music>
//private lateinit var  musiclist3 : ArrayList<Music>
//private lateinit var  musiclist4 : ArrayList<Music>
//class MusicTitle1 : Fragment() {
//    private lateinit var adapter: TitleAdapter
//    private lateinit var viewModel: MainViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_title,container,false)
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        val recyclerview : RecyclerView = view?.findViewById<RecyclerView>(R.id.title_recyclerview)!!
//        val activity = activity as Activity
//        val layoutmanager = LinearLayoutManager(activity)
//        recyclerview.layoutManager = layoutmanager
//        adapter = TitleAdapter(musiclist1,{})
//        recyclerview.adapter = adapter
//    }
//    override fun onResume() {
//        super.onResume()
//        val recyclerview : RecyclerView? = view?.findViewById(R.id.title_recyclerview)
//        if (recyclerview != null) {
//            // 重新创建adapter，确保数据引用最新
//            adapter = TitleAdapter(musiclist1,{})
//            recyclerview.adapter = adapter
//        }
//    }
//}
//class MusicTitle2 : Fragment() {
//    private lateinit var adapter: TitleAdapter
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_title,container,false)
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        val recyclerview : RecyclerView = view?.findViewById<RecyclerView>(R.id.title_recyclerview)!!
//        val activity = activity as Activity
//        val layoutmanager = LinearLayoutManager(activity)
//        recyclerview.layoutManager = layoutmanager
//        adapter = TitleAdapter(musiclist2,{})
//        recyclerview.adapter = adapter
//    }
//    override fun onResume() {
//        super.onResume()
//        if (::adapter.isInitialized) {
//            adapter.notifyDataSetChanged()
//        }
//    }
//}
//
//class MusicTitle3 : Fragment() {
//    private lateinit var adapter: TitleAdapter
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_title,container,false)
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        val recyclerview : RecyclerView = view?.findViewById<RecyclerView>(R.id.title_recyclerview)!!
//        val activity = activity as Activity
//        val layoutmanager = LinearLayoutManager(activity)
//        recyclerview.layoutManager = layoutmanager
//        adapter = TitleAdapter(musiclist3,{})
//        recyclerview.adapter = adapter
//    }
//    override fun onResume() {
//        super.onResume()
//        if (::adapter.isInitialized) {
//            adapter.notifyDataSetChanged()
//        }
//    }
//}
//class MusicTitle4 : Fragment() {
//    private lateinit var adapter: TitleAdapter
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_title,container,false)
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        val recyclerview : RecyclerView = view?.findViewById<RecyclerView>(R.id.title_recyclerview)!!
//        val activity = activity as Activity
//        val layoutmanager = LinearLayoutManager(activity)
//        recyclerview.layoutManager = layoutmanager
//        adapter = TitleAdapter(musiclist4,{})
//        recyclerview.adapter = adapter
//    }
//    override fun onResume() {
//        super.onResume()
//        if (::adapter.isInitialized) {
//            adapter.notifyDataSetChanged()
//        }
//    }
//}
//class MusicTitle5 : Fragment() {
//    private lateinit var adapter: TitleAdapter
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_title,container,false)
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//        val recyclerview : RecyclerView = view?.findViewById<RecyclerView>(R.id.title_recyclerview)!!
//        val activity = activity as Activity
//        val layoutmanager = LinearLayoutManager(activity)
//        recyclerview.layoutManager = layoutmanager
//        adapter = TitleAdapter(Testinit1())
//        recyclerview.adapter = adapter
//    }
//    override fun onResume() {
//        super.onResume()
//        if (::adapter.isInitialized) {
//            adapter.notifyDataSetChanged()
//        }
//    }
//}
//class MusicTitle6 : Fragment() {
//    private lateinit var adapter: TitleAdapter
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_title,container,false)
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        val recyclerview : RecyclerView = view?.findViewById<RecyclerView>(R.id.title_recyclerview)!!
//        val activity = activity as Activity
//        val layoutmanager = LinearLayoutManager(activity)
//        recyclerview.layoutManager = layoutmanager
//        adapter = TitleAdapter(Testinit1())
//        recyclerview.adapter = adapter
//    }
//    override fun onResume() {
//        super.onResume()
//        if (::adapter.isInitialized) {
//            adapter.notifyDataSetChanged()
//        }
//    }
//}
//class MusicTitle7 : Fragment() {
//    private lateinit var adapter: TitleAdapter
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_title,container,false)
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        val recyclerview : RecyclerView = view?.findViewById<RecyclerView>(R.id.title_recyclerview)!!
//        val activity = activity as Activity
//        val layoutmanager = LinearLayoutManager(activity)
//        recyclerview.layoutManager = layoutmanager
//        adapter = TitleAdapter(Testinit1())
//        recyclerview.adapter = adapter
//    }
//    override fun onResume() {
//        super.onResume()
//        if (::adapter.isInitialized) {
//            adapter.notifyDataSetChanged()
//        }
//    }
//}
//fun getmusic(musiclist_1 : ArrayList<Music>,musiclist_2 : ArrayList<Music>,musiclist_3 : ArrayList<Music>,musiclist_4 : ArrayList<Music>){
//    musiclist1 = musiclist_1
//    musiclist2 = musiclist_2
//    musiclist3 = musiclist_3
//    musiclist4 = musiclist_4
//}