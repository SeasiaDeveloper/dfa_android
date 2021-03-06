package com.dfa.ui.comments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.dfa.R
import com.dfa.adapters.CommentsAdapter
import com.dfa.customviews.CenteredToolbar
import com.dfa.pojo.response.DeleteComplaintResponse
import com.dfa.pojo.response.GetCommentsResponse
import com.dfa.pojo.response.GetProfileResponse
import com.dfa.ui.comments.presenter.CommentsPresenter
import com.dfa.ui.comments.presenter.CommentsPresenterImplClass
import com.dfa.ui.comments.view.CommentsView
import com.dfa.ui.generalpublic.view.GeneralPublicHomeFragment
import com.dfa.ui.mycases.MyCasesActivity
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.activity_comments.*


class CommentsActivity : AppCompatActivity(), CommentsView {

    private lateinit var mContext: Context
    private var presenter: CommentsPresenter = CommentsPresenterImplClass(this@CommentsActivity)
    private var commentsList: List<GetCommentsResponse.CommentData> = mutableListOf()
    var token: String = ""
    var id: String = ""
    var itemCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        setupUI()
    }

    fun setupUI() {
        mContext = this@CommentsActivity
        token = PreferenceHandler.readString(mContext, PreferenceHandler.AUTHORIZATION, "")!!
        id = intent.getStringExtra("id")

        //(toolbarLayout as CenteredToolbar).title = getString(R.string.add_comments)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        GeneralPublicHomeFragment.change = 0
        GeneralPublicHomeFragment.fromIncidentDetailScreen = 0
        MyCasesActivity.change = 0
        val value = PreferenceHandler.readString(this, PreferenceHandler.PROFILE_JSON, "")
        val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
        if (jsondata != null) {
            if (jsondata.data?.profile_pic != null) {
              try{  Glide.with(this).load(jsondata.data.profile_pic).into(imgProfile)}catch (e:Exception){
                  e.printStackTrace()
              }
            }
        }


        val horizontalLayoutManager = LinearLayoutManager(
            mContext,
            RecyclerView.VERTICAL, false
        )
        rvComments.layoutManager = horizontalLayoutManager

        Utilities.showProgress(mContext)
        presenter.fetchComments(id, token)

        tvPost.setOnClickListener {
            if (etComments.text.toString().isEmpty()) {
                Utilities.showMessage(
                    mContext,
                    getString(R.string.enter_comments_validation_message)
                )
            } else {
                Utilities.showProgress(mContext)
                presenter.onAddComment(token, id, etComments.text.toString()) //to add the comment
            }
        }
    }

    override fun onGetCommentsSuccess(response: GetCommentsResponse) {
        //gets response containing list of comments
        Utilities.dismissProgress()
        commentsList = response.data!!
        if (commentsList.isNotEmpty()) {
            tvRecord.visibility = View.GONE
            rvComments.visibility = View.VISIBLE
            rvComments.adapter = CommentsAdapter(this, commentsList.toMutableList())
            MyCasesActivity.commentsCount = rvComments.adapter?.itemCount!!
            GeneralPublicHomeFragment.commentsCount = rvComments.adapter?.itemCount!!
        } else {
            tvRecord.visibility = View.VISIBLE
            rvComments.visibility = View.GONE
        }
    }

    override fun onCommentsAddedSuccess(response: DeleteComplaintResponse) {
        etComments.text?.clear()
       // Utilities.showMessage(mContext, response.message!!)
        //refresh the list
        presenter.fetchComments(id, token)
        //change = 1
        GeneralPublicHomeFragment.commentChange = id.toInt()
        //MyCasesActivity.change=1
        MyCasesActivity.commentChange = id.toInt()
        MyCasesActivity.isfirst=true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        /* val intent = Intent()
         intent.putExtra("ID", id)
         setResult(2, intent)
         finish()*/
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(mContext, error)
    }

}
