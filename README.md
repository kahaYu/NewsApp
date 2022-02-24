# NewsApp

Be informed about **World Breaking News!** <br>
**News App!** for your disposal. :open_hands:

## Technologies I used:
1. Retrofit + OkHttp;
2. MVVM + Room + ViewModel;
3. RecyclerView + Adapter;
4. Navigation Component + Fragments;
5. Pagination;
6. Coroutines;
7. LiveData;
8. ViewBinding;
9. Landscape option;
10. NightMode.

:eyes: Look at the **pictures** for detalization. <br>

## What it does?

**:one: When you launch the App, ViewModel queries API for breaking news.** <br>
App contains one activity and tree main fragments. + one fragment to read article with WebView. <br>
You navigate via bottom navigation bar.

<img src="https://user-images.githubusercontent.com/79222385/155553292-0e4e7541-be77-4da1-8808-ffbb95931354.jpg" width="360" height="640">

**:two: Tap the article and read the whole content.** <br>
Tap fab to add to favorites. Appropriate toast will appear.<br>

<img src="https://user-images.githubusercontent.com/79222385/155553735-fc3dd39e-bf48-437a-ab59-a43ccac5849f.jpg" width="360" height="640">

**:three: Navigate to saved articles.** <br>
Room handles local database.<br>

<img src="https://user-images.githubusercontent.com/79222385/155554152-3f740c23-0746-4aeb-a804-d96b31155a2f.jpg" width="360" height="640">

**:four: Swipe to delete saved article.** <br>
Rest of articles smoothly fill empty spase. It's possible with DiffUtil callback. <br>
Sure, you can recover deleted article.

<img src="https://user-images.githubusercontent.com/79222385/155554417-76ffada3-74cc-4d97-87eb-1a6e79e46dcb.jpg" width="230" height="409"> <img src="https://user-images.githubusercontent.com/79222385/155554486-8c6c3dc8-1d1b-45c8-bb69-497c73ff9580.jpg" width="230" height="409"> <img src="https://user-images.githubusercontent.com/79222385/155554519-9f32d05b-6127-4db8-bffe-1b0cc4abe41d.jpg" width="230" height="409"> 

**:five: Search is also possible!** <br>
It happens on the background via coroutines. Beautiful loader view is shown up. <br>
Keyboard goes away when you start scrolling.

<img src="https://user-images.githubusercontent.com/79222385/155555164-e6daee1e-1fc7-4a6c-8829-0a39889c762e.jpg" width="230" height="409"> <img src="https://user-images.githubusercontent.com/79222385/155555190-3d7ad4b2-d778-4092-a6b7-09c3a359b601.jpg" width="230" height="409"> <img src="https://user-images.githubusercontent.com/79222385/155555212-414b99da-434c-419a-a453-b99c2f3ee7ab.jpg" width="230" height="409"> 

**:six: My proud - pagination!** <br>
To increase speed of app, api loads only 20 results at once. When you achieve lower border of results, api calls again and returns another 20 results.<br>
Works only in one direction. The same can be realized for the upper direction if needed.<br>
Again, nice progress bar appears :heart_eyes: 

<img src="https://user-images.githubusercontent.com/79222385/155558669-45b849a0-935d-4433-abc1-4015c26c66ab.jpg" width="360" height="640">

**:seven: Custom layout for landscape. And pretty night theme! :waxing_crescent_moon:** <br>
Theme could be set manually in app, but generally it tightly coupled with system's theme option.<br>
In landscape mode app shows only header of article to accommodate more articles on the screen.<br>

<img src="https://user-images.githubusercontent.com/79222385/155558978-07d1424b-1ea3-41cb-87fb-392d05e0193f.jpg" width="409" height="230"> <img src="https://user-images.githubusercontent.com/79222385/155558993-cdbafef3-775e-49e8-87d3-350bfb60b07d.jpg" width="409" height="230"> 

**:eight: WebView supports night theme also.** <br>
It is realized separately from general theme. Because WebView needs special approach. 

<img src="https://user-images.githubusercontent.com/79222385/155562183-e8248fab-57a3-4773-a667-37d1b24f362b.jpg" width="360" height="640">

**:nine: Ripple indicates touch event on article or navigation button.** <br>
For proper appearance of ripple the mask is needed to round the corners.

<img src="https://user-images.githubusercontent.com/79222385/155562303-2e4ed970-f583-4347-8fb0-a09434b88cc6.jpg" width="230" height="409"> <img src="https://user-images.githubusercontent.com/79222385/155562314-258a9d89-5f77-4a58-9176-5c6e38e7b253.jpg" width="230" height="409"> <img src="https://user-images.githubusercontent.com/79222385/155562325-8ed3f642-760d-470a-b3f5-a07b6999a77c.jpg" width="230" height="409">

**:one::zero: If internet is missing you are notified.** <br>

<img src="https://user-images.githubusercontent.com/79222385/155563439-4c65f4a7-6d74-48a6-a283-9bf6aa8d3230.jpg" width="360" height="640">

## Most challenging issues I had:

1. To create logic when it's needed to **paginate** or not. A dozen of mediator values was introduced, but no way to make it easier without third-party libraries.<br>

2. **Day/Night** modes were new for me. It took much time to sort out differences between styles, themes, colors. :weary: <br>
But now it looks rather simple after all.<br>
And after my hard efforts to set up theming, it turned out WebView doesn't support it out of box. :sob:<br>
I solved it by defining the current theme of app in ArticleFragment and depending on it I turn on/off the ForceDark mode of WebView.

3. It might sound weird, but much time was spent **to remove SoftKeyboard** when no need of it. :confused: <br>
After all I found an elegant solution :bowtie: - to tie appearance of keyboard with lose of focus of EditText view. <br>
For it I wrote two extention functions for Context and Fragment classes o hide keyboard and hung focus listener in SearchFragment.
Plus 1 pattern solution to my utils repo :blush:

### Thank you for attention! :raised_hands:
:chart: Let the news be only positive and motivating. 

