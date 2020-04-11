package com.ngo.ui.comments

import android.R.id.message
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.ngo.R
import com.ngo.adapters.CommentsAdapter
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.response.DeleteComplaintResponse
import com.ngo.pojo.response.GetCommentsResponse
import com.ngo.pojo.response.GetProfileResponse
import com.ngo.ui.comments.presenter.CommentsPresenter
import com.ngo.ui.comments.presenter.CommentsPresenterImplClass
import com.ngo.ui.comments.view.CommentsView
import com.ngo.ui.generalpublic.view.GeneralPublicHomeFragment
import com.ngo.ui.home.fragments.cases.CasesFragment
import com.ngo.ui.home.fragments.cases.CasesFragment.Companion.change
import com.ngo.ui.home.fragments.cases.CasesFragment.Companion.commentChange
import com.ngo.ui.mycases.MyCasesActivity
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_comments.*


class CommentsActivity : AppCompatActivity(), CommentsView {

    private lateinit var mContext: Context
    private var presenter: CommentsPresenter = CommentsPresenterImplClass(this@CommentsActivity)
    private var commentsList: List<GetCommentsResponse.CommentData> = mutableListOf()
    var token: String = ""
    var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        setupUI()
    }

    fun setupUI() {
        mContext = this@CommentsActivity
        token = PreferenceHandler.readString(mContext, PreferenceHandler.AUTHORIZATION, "")!!
        id = intent.getStringExtra("id")

        (toolbarLayout as CenteredToolbar).title = getString(R.string.add_comments)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        GeneralPublicHomeFragment.change =0
        GeneralPublicHomeFragment.fromIncidentDetailScreen=0
        CasesFragment.fromIncidentDetailScreen=0
        MyCasesActivity.change=0
        change=0
        val value = PreferenceHandler.readString(this, PreferenceHandler.PROFILE_JSON, "")
        val jsondata = GsonBuilder().create().fromJson(value, GetProfileResponse::class.java)
        if (jsondata != null) {
            if (jsondata.data?.profile_pic != null) {
                Glide.with(this).load(jsondata.data.profile_pic).into(imgProfile)
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
        } else {
            tvRecord.visibility = View.VISIBLE
            rvComments.visibility = View.GONE
        }
    }

    override fun onCommentsAddedSuccess(response: DeleteComplaintResponse) {
        etComments.text?.clear()
        Utilities.showMessage(mContext, response.message!!)
        //refresh the list
        presenter.fetchComments(id, token)
        //change = 1
        commentChange=id.toInt()
        GeneralPublicHomeFragment.commentChange=id.toInt()
        //MyCasesActivity.change=1
        MyCasesActivity.commentChange=id.toInt()
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
