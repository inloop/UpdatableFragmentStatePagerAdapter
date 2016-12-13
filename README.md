UpdatableFragmentStatePagerAdapter
================

This is an improved version of the original [FragmentStatePagerAdapter](https://developer.android.com/reference/android/support/v4/app/FragmentStatePagerAdapter.html) with important changes that add support for changing the order and count of items.

Two major problems exist in the FragmentStatePagerAdapter which make it impossible to use with ```notifyDataSetChanged()```, these are desribed as part of our article [Adventures with FragmentStatePagerAdapter](Adventures with FragmentStatePagerAdapter):

- State bundles are assigned to wrong Fragments after the dataset order has changed
http://speakman.net.nz/blog/2014/02/20/a-bug-in-and-a-fix-for-the-way-fragmentstatepageradapter-handles-fragment-restoration/
- Crash in case the order of items is changed
http://billynyh.github.io/blog/2014/03/02/fragment-state-pager-adapter/

You can build and look at the [sample implementation](sample/src/main/java/eu/inloop/pager/sample/MainActivity.java).

How to use
--------

The implemenation is the same as with the original FragmentStatePagerAdapter. 
You need to override two methods in order to support dataset changes:

```java
@Override
public int getItemPosition(Object object) {
    YourFragment fragment = (YourFragment) object;
    // ... determine the position of the fragment in your dataset 
    // ... e.g. extract some ID from Arguments and search in your dataset
}
```
You need to return POSITION_NONE in case the dataset doesn't contain the item anymore.
Always returning POSITION_NONE will work but will have an impact on performance (current Fragments are recreated even if the position hasn't changed). 

Also you need to return a unique identifier for an item based on the position. This isually means returning an "ID" or hashcode of the item.
```java
@Override
public long getItemId(int position) {
    // get the item in your dataset on this position and return some ID or hash code.
}
```

Download
--------

```groovy
compile 'eu.inloop:pageradapter:0.1.0'
```
