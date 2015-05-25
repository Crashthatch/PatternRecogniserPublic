To generate a new metaModel, load modelLearner.rmp in Rapidminer GUI. Select the CSV exported from MySQL, modify to use different models, convert the features to different types, etc.
Run it to produce latest-learned-model.mod and sample-input-atts.aml (which contains mappings for the training & test set).
If you want to use that newly generated model, copy these files up one directory over the top of:
"model-to-apply.mod" and "sample-input-atts.mod".

RMRelationship ranker will use the attributes in the sample-input-atts and the new model to produce predictions, and the RelationshipSelector will select only the top ones.

You can not do preprocessing in the modelLearner beyond changing the attributes & mappings because they won't be reflected in the test set (the attributes are copied from "features" into the testset columns with matching names).
If you need to do preprocessing, either do it at feature generation time or modify the model-applier.rmp process to do the same preprocessing steps as were performed during training.
