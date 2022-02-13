package com.overman.basicapp

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.AppBarLayout
import com.overman.basicapp.databinding.ActivityMainBinding
import com.overman.basicapp.databinding.ItemTodoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.include_layout_progressbar.view.*
import kotlinx.android.synthetic.main.include_layout_toolbar.view.*


enum class MenuLeft {
    TYPE1,
}

enum class MenuRight {
    TYPE1,
}


abstract class BaseActivity<T : ViewBinding> : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: T
    var appBar: AppBarLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getInflater()
        setContentView(getBindingRoot())
        initView()
    }

    fun getBindingRoot(): View {
        try {
            appBar = binding.root.appbar
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun setWhiteTransparentBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            window.statusBarColor = Color.WHITE
            val view = window.decorView
            view.systemUiVisibility =
                view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun showProgress() {
        appBar?.pbProgressbar?.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        appBar?.pbProgressbar?.visibility = View.GONE
    }

    fun setAppBarLeftMenuType(type: MenuLeft) {
        when (type) {
            MenuLeft.TYPE1 -> {
                appBar?.llToolbarLeftMenuType1?.visibility = View.VISIBLE
                appBar?.btBackButton?.setOnClickListener(this)
            }
        }
    }

    fun setAppBarRightMenuType(type: MenuRight) {

    }

    fun setAppbarTitle(text: String) {
        appBar?.tvToolbarTitle?.text = text
    }

    abstract fun getInflater() : T
    abstract fun initView()
}

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val model: TodoViewModel by viewModels()

    var remoteRecyclerAdapter: TodoRecyclerAdapter? = null
    var localRecyclerAdapter: TodoRecyclerAdapter? = null

    override fun getInflater() = ActivityMainBinding.inflate(layoutInflater)

    override fun initView() {
        setAppbarTitle("타이틀")
        setAppBarLeftMenuType(MenuLeft.TYPE1)
        setAppBarRightMenuType(MenuRight.TYPE1)

        binding.rvRemoteDataSource.layoutManager = GridLayoutManager(this, 1)
        binding.rvRemoteDataSource.addItemDecoration(GridSpacingItemDecoration(1, 20, true))
        model.todoRemoteData.observe(this, Observer {
            remoteRecyclerAdapter = TodoRecyclerAdapter(it as MutableList<Todo>, object : ListItemAction<Todo> {
                override fun itemClick(item: Todo) {
                    model.addLocalData(item) {
                        remoteRecyclerAdapter?.deleteItem(item)
                        localRecyclerAdapter?.addItem(item)
                    }
                }
            })
            binding.rvRemoteDataSource.adapter = remoteRecyclerAdapter
        })

        binding.rvLocalDataSource.layoutManager = GridLayoutManager(this, 1)
        binding.rvLocalDataSource.addItemDecoration(GridSpacingItemDecoration(1, 20, true))
        model.todoLocalData.observe(this, Observer {
            localRecyclerAdapter = TodoRecyclerAdapter(
                it as MutableList<Todo>,
                object : ListItemAction<Todo> {
                    override fun itemClick(item: Todo) {
                        model.deleteLocalData(item) {
                            remoteRecyclerAdapter?.addItem(item)
                            localRecyclerAdapter?.deleteItem(item)
                        }
                    }
                })
            binding.rvLocalDataSource.adapter = localRecyclerAdapter
        })
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btBackButton -> {
                finish()
            }
        }
    }

}

interface ListItemAction<T> {
    fun itemClick(item: T)
}

class TodoRecyclerAdapter(
    private val todoList: MutableList<Todo>,
    private var listItemAction: ListItemAction<Todo>? = null
) : RecyclerView.Adapter<TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context))
        return TodoViewHolder(binding, listItemAction)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(todoList[position])
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun addItem(item: Todo) {
        todoList.add(item)
        notifyDataSetChanged()
    }

    fun deleteItem(item: Todo) {
        todoList.remove(item)
        notifyDataSetChanged()
    }
}

class TodoViewHolder(private val binding: ItemTodoBinding, private var listItemAction: ListItemAction<Todo>? = null) : RecyclerView.ViewHolder(binding.root) {

    fun bind(todo: Todo) {
        binding.todo = todo
        binding.viewHolder = this
    }

    fun itemClick(view: View) {
        Toast.makeText(view.context, "btnClick + ${binding.todo?.id}", Toast.LENGTH_SHORT).show()
        listItemAction?.itemClick(binding.todo!!)
    }

}