package com.ngo.ui.generalpublic.presenter

import android.content.Context
import com.ngo.pojo.request.ComplaintRequest
import com.ngo.pojo.response.ComplaintResponse
import com.ngo.pojo.response.GetCrimeTypesResponse
import com.ngo.ui.generalpublic.model.PublicModel
import com.ngo.ui.generalpublic.view.PublicComplaintView

class PublicComplaintPresenterImpl(private var complaintsView: PublicComplaintView) :
    PublicComplaintPresenter {
    private var complaintsModel: PublicModel = PublicModel(this)


    override fun onEmptyLevel() {
        complaintsView.showEmptyLevelError()
    }

    override fun onEmptyImage() {
        complaintsView.showEmptyImageError()
    }

    override fun onEmptyDescription() {
        complaintsView.showEmptyDescError()
    }

    override fun onValidationSuccess() {
        complaintsView.onValidationSuccess()
    }

    override fun onGetCrimeTypes(token: String?) {
        complaintsModel.getCrimeTypesList(token)
    }

    override fun onGetCrimeTypesSuccess(getCrimeTypesResponse: GetCrimeTypesResponse) {
        complaintsView.getCrimeTypesListSuccess(getCrimeTypesResponse)
    }

    override fun onGetCrimeTypesFailure(error: String) {
        complaintsView.getCrimeTyepLstFailure(error)
    }

    override fun checkValidations(level: Int, image: ArrayList<String>, description: String) {
        complaintsModel.setValidation(level, image, description)
    }

    override fun onSaveDetailsSuccess(response: ComplaintResponse) {
        complaintsView.showComplaintsResponse(response)
    }

    override fun onSaveDetailsFailed(error: String) {
        complaintsView.showServerError(error)
    }

    override fun saveDetailsRequest(token: String?, request: ComplaintRequest,context: Context) {
        complaintsModel.complaintsRequest(token, request,context)
    }

    override fun showError(error: String) {
        complaintsView.showServerError(error)
    }

}