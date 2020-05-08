package com.ngo.ui.commentlikelist

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ngo.R
import com.ngo.adapters.CommentsAdapter
import com.ngo.adapters.LikesAdapter
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.response.GetCommentsResponse
import com.ngo.ui.commentlikelist.presenter.CommentLikeUsersPresenter
import com.ngo.ui.commentlikelist.presenter.CommentLikeUsersPresImpl
import com.ngo.ui.commentlikelist.view.CommentLikeUserView
import com.ngo.ui.generalpublic.view.GeneralPublicHomeFragment
import com.ngo.ui.home.fragments.cases.CasesFragment
import com.ngo.ui.mycases.MyCasesActivity
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_comment_like_users_list.*

class CommentLikeUsersList : AppCompatActivity(), CommentLikeUserView {

    private lateinit var mContext: Context
    var token: String = ""
    var id: String = ""
    var listFor: String = ""
    private var presenter: CommentLikeUsersPresenter = CommentLikeUsersPresImpl(this@CommentLikeUsersList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_like_users_list)
        setupUI()
    }

    private fun setupUI() {
        mContext = this@CommentLikeUsersList
        token = PreferenceHandler.readString(mContext, PreferenceHandler.AUTHORIZATION, "")!!
        id = intent.getStringExtra("id")
        listFor = intent.getStringExtra("for")

        if (listFor.equals("liked")) {
            (toolbarLayout as CenteredToolbar).title = getString(R.string.liked_users)
            Utilities.showProgress(this@CommentLikeUsersList)
            presenter.getLikeList(id, token)
        } else {
            (toolbarLayout as CenteredToolbar).title = getString(R.string.commented_users)
            Utilities.showProgress(this@CommentLikeUsersList)
            presenter.getCommentList(id, token)
        }

        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        GeneralPublicHomeFragment.change = 0
        GeneralPublicHomeFragment.fromIncidentDetailScreen = 0
        CasesFragment.fromIncidentDetailScreen = 0
        MyCasesActivity.change = 0
        CasesFragment.change = 0

        val linearLayoutManager = LinearLayoutManager(this@CommentLikeUsersList, RecyclerView.VERTICAL, false)
        rvComments.layoutManager = linearLayoutManager

    }

    override fun onGetCommentsListSuccess(response: GetCommentsResponse) {
        Utilities.dismissProgress()
        val commentUsersList = response.data!!
        if (commentUsersList.isNotEmpty()) {
            tvRecord.visibility = View.GONE
            rvComments.visibility = View.VISIBLE
            rvComments.adapter = CommentsAdapter(this, commentUsersList.toMutableList())
        } else {
            tvRecord.visibility = View.VISIBLE
            rvComments.visibility = View.GONE
        }
    }

    override fun onGetLikeListSuccess(response: GetCommentsResponse) {
        Utilities.dismissProgress()
        val likesUsersList = response.data!!
        if (likesUsersList.isNotEmpty()) {
            tvRecord.visibility = View.GONE
            rvComments.visibility = View.VISIBLE
            rvComments.adapter = LikesAdapter(this, likesUsersList.toMutableList())
        } else {
            tvRecord.visibility = View.VISIBLE
            rvComments.visibility = View.GONE
        }
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
        Utilities.showMessage(mContext, error)
    }
}
