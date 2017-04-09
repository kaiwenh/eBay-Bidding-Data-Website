We bulid three index:

Name  Field.Store.YES
UserID  Field.Store.YES
Name + Catogary + Description  Field.Store.No

We build Name and UserID because we need these as result to return.

We build Name + Catogary + Description because the search should be performed over the union of the item name, category, and description attributes. Since we do not need to retrieve this content, the Field.Store can be NO.
