package com.dfa.ui.generalpublic.presenter



import android.content.Context
import com.dfa.pojo.request.ComplaintRequest
import com.dfa.pojo.response.ComplaintResponse
import com.dfa.pojo.response.DistResponse
import com.dfa.pojo.response.GetCrimeTypesResponse
import com.dfa.pojo.response.PStationsListResponse
import com.dfa.ui.generalpublic.NearByPoliceStationActivity
import com.dfa.ui.generalpublic.model.PublicModel
import com.dfa.ui.generalpublic.view.PublicComplaintView

class publicComplaintPresenterNearBy(private var complaintsView: NearByPoliceStationActivity) :
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

    override fun districtsSuccess(response: DistResponse) {
        complaintsView.getDistrictsSuccess(response)
    }

    override fun hitDistricApi() {
        complaintsModel.getDist()

    }

    override fun stationsSuccess(response: PStationsListResponse) {
        complaintsView.getpStationSuccess(response)

    }


    override fun hitpstationApi(distId:String) {
        complaintsModel.getpStation(distId)

    }

    override fun saveDetailsRequest(token: String?, request: ComplaintRequest,context: Context) {
        complaintsModel.complaintsRequest(token, request,context)
    }

    override fun showError(error: String) {
        complaintsView.showServerError(error)
    }

}