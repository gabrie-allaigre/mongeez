package com.talanlabs.mongeez.validation;

import com.talanlabs.mongeez.commands.ChangeSet;

import java.util.List;

public interface ChangeSetsValidator {

    void validate(List<ChangeSet> changeSets) throws ValidationException;

}
