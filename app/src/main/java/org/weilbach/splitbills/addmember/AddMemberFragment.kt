package org.weilbach.splitbills.addmember

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.weilbach.splitbills.R
import org.weilbach.splitbills.databinding.FragmentAddmemberBinding
import org.weilbach.splitbills.util.setupSnackbar

class AddMemberFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentAddmemberBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /*viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }*/
        setupActionBar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_addmember, container, false)
        viewDataBinding = FragmentAddmemberBinding.bind(root).apply {
            viewmodel = (activity as AddMemberActivity).obtainViewModel()
        }
        viewDataBinding.setLifecycleOwner(this.viewLifecycleOwner)
        setHasOptionsMenu(true)
        retainInstance = false
        return viewDataBinding.root
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).supportActionBar?.setTitle(
                if (arguments != null && arguments?.get(ARGUMENT_CREATE_PROFILE) != null)
                    R.string.frag_add_member_create_profile
                else
                    R.string.frag_add_member_add_member
        )
    }

    companion object {
        const val ARGUMENT_CREATE_PROFILE = "CREATE_PROFILE"

        fun newInstance() = AddMemberFragment()
    }
}