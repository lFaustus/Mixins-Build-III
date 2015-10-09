package com.fluxinated.mixins;

import com.fluxinated.mixins.enums.FragmentTags;

/**
 * Created by flux on 6/18/15.
 */
public interface OnFragmentChangeListener {
    void OnFragmentChange(FragmentTags fragment, Object... extra);
}
