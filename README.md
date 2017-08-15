# Smoosh
Smoosh is a Tinder-like dating app for Android which several distinguishing features:
- Smoosh is %100 free and always will be: no premium memberships, no in-app purchases, and no ads.
- Smoosh allows each member to 'review' each other member with 'tags', which then appear on that user's profile page.
- Smoosh encourages users to physically meet up by rewarding them for checking in when their devices are nearby each other.

## Tagging
![A preview of the tagging feature](https://github.com/BrianLandes/Smoosh/blob/master/sample_images/tagging.gif)

Members can quickly apply one or several tags to other members. The most given and most recent tags will show up on that member's profile page. 

![Tags appearing on a profile](https://github.com/BrianLandes/Smoosh/blob/master/sample_images/viewing_tags.gif)

## Dependencies
Smoosh stands on the shoulders of giants, using open source libraries found around the web.
- [FragmentAnimations](https://android-arsenal.com/details/1/3526) for slick transitions between various screens

![FragmentAnimations being used in Smoosh](https://github.com/BrianLandes/Smoosh/blob/master/sample_images/FragmentAnimationsDemo.gif)
- [MyDynamicToast](https://android-arsenal.com/details/1/4798) for vibrant heads-up notifications
- [uCrop](https://android-arsenal.com/details/1/3054) for manipulating and uploading profile pictures

![uCrop being used in Smoosh](https://github.com/BrianLandes/Smoosh/blob/master/sample_images/uCropDemo.gif)
- [MaterialEditText](https://android-arsenal.com/details/1/1085) for feature-rich editable text fields

![MaterialEditText being used in Smoosh](https://github.com/BrianLandes/Smoosh/blob/master/sample_images/MaterialEditTextDemo.gif)
- [DismissibleImageView](https://android-arsenal.com/details/1/5766) for flexible full-screen images

![DismissibleImageView being used in Smoosh](https://github.com/BrianLandes/Smoosh/blob/master/sample_images/DismissibleImageViewDemo.gif)
- [CarouselView](https://android-arsenal.com/details/1/3289) for displaying a scrolling slide-show of profile photos

![CarouselView being used in Smoosh](https://github.com/BrianLandes/Smoosh/blob/master/sample_images/CarouselViewDemo.gif)
- [AwesomeBar](https://android-arsenal.com/details/1/5226) for providing a consistent and convenient options bar
- [ChipCloud](https://android-arsenal.com/details/1/5246) for listing tags
- [GeoFire](https://github.com/firebase/geofire-java) for tracking members' locations and querying the members around them
- [Firebase](https://firebase.google.com/) for authenticating users, managing online storage, and keeping information in a database
- [FirebaseUI](https://github.com/firebase/FirebaseUI-Android/tree/master/auth) for handling the authentication UI

