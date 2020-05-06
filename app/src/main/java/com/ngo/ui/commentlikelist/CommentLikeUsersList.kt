package com.ngo.ui.commentlikelist

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ngo.R
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.response.GetCommentsResponse
import com.ngo.ui.comments.presenter.CommentsPresenter
import com.ngo.ui.comments.presenter.CommentsPresenterImplClass
import com.ngo.ui.generalpublic.view.GeneralPublicHomeFragment
import com.ngo.ui.home.fragments.cases.CasesFragment
import com.ngo.ui.mycases.MyCasesActivity
import com.ngo.utils.PreferenceHandler
import kotlinx.android.synthetic.main.activity_comments.*

class CommentLikeUsersList : AppCompatActivity() {
    private lateinit var mContext: Context
    //private var presenter: CommentsPresenter = CommentsPresenterImplClass(this@CommentLikeUsersList)
    // private var commentsList: List<GetCommentsResponse.CommentData> = mutableListOf()
    var token: String = ""
    var id: String = ""
    var listFor: String = ""
    var itemCount: Int = 0

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

        if(listFor.equals("liked")){
            (toolbarLayout as CenteredToolbar).title = getString(R.string.liked_users)
        }else{
            (toolbarLayout as CenteredToolbar).title = getString(R.string.commented_users)
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

    }
}
