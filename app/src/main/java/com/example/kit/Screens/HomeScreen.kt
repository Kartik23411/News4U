package com.example.kit.Screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kit.Constants
import com.example.kit.R
import com.example.kit.data.Article
import com.example.kit.ui.theme.dongle_font
import com.example.kit.ui.theme.lobster_two_font
import com.example.kit.viewmodal.HomeViewModal
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModal: HomeViewModal = viewModel()){

    val isLoading = viewModal.isLoading.value
    val errorMessage = viewModal.errorMessage.value
    val newsList = viewModal.articleList.value

    val context = LocalContext.current

    val q = remember { mutableStateOf("General") }
    val language = remember { mutableStateOf("eng") }
    val country = remember { mutableStateOf("in") }
    val sortBy = remember { mutableStateOf("relevance") }
    val noOfNews by remember { mutableIntStateOf(100) }

    LaunchedEffect(Unit) {
        viewModal.fetchNews(q.value, language.value, country.value, sortBy.value, noOfNews)
    }

    // for modalbottomsheet
    val scope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState()
    var selectedArticle by remember{ mutableStateOf<Article?>(null) }
    val bgColor = if (isSystemInDarkTheme()) Color.Black else Color(0xFFE6E2DA)

        if (isLoading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                    strokeWidth = ProgressIndicatorDefaults.CircularStrokeWidth
                )
                Text("Hold on, getting data", fontSize = 12.sp, color = Color(0xFFE6E2DA))
            }
        } else {
            Scaffold(
                floatingActionButton = { FloatingActionButton(
                    modifier = Modifier.padding(bottom = 32.dp, end = 16.dp),
                    containerColor = MaterialTheme.colorScheme.background,
                    onClick = {viewModal.fetchNews(q.value,language.value,country.value,sortBy.value,noOfNews)},
                    content = { Icon(imageVector = Icons.Default.Refresh, contentDescription = "image") }
                ) },
                floatingActionButtonPosition = FabPosition.EndOverlay,
                topBar = { TopBarWithDropdowns(q, country, language, sortBy) },
                containerColor = bgColor,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                LazyColumn(modifier = Modifier.padding(it)) {
                    items(newsList) { article ->
                        NewsCard(article, {
                            selectedArticle = article
                            Log.e("click" , "item clicked")
                            scope.launch { modalSheetState.show() }
                        })
                    }
                }

                selectedArticle?.let {
                    Log.e("error", " sheet opened")
                    ModalBottomSheet(
                        sheetState = modalSheetState,
                        modifier = Modifier.fillMaxWidth(),
                        onDismissRequest = {
                            scope.launch {
                                modalSheetState.hide() }
                                selectedArticle = null
                        },
                        properties = ModalBottomSheetProperties(
                            shouldDismissOnBackPress = true,
                            securePolicy = SecureFlagPolicy.SecureOn,
                            isFocusable = true
                        ),
                        tonalElevation = 5.dp,
                        containerColor = bgColor,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        MoreBottomSheet(selectedArticle!!, context)
                    }
                }
            }
        }
    }

//}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsCard(
    article: Article,
    onClick:() -> Unit
){
    val contentColor = if(isSystemInDarkTheme()) Color.White else Color.Black
    val cardColor = if (isSystemInDarkTheme()) Color(0xFF1B2124) else Color(0xFFFDFDFD)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(cardColor),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column (
            Modifier
                .fillMaxWidth()
                .padding(8.dp)){
            Text(
                article.title,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                fontSize = 16.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Text(
                article.description,
                fontWeight = FontWeight.Thin,
                color = contentColor.copy(alpha = .6f),
                fontSize = 12.sp,
                lineHeight = 15.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3
            )
        }
        Row(
            modifier = Modifier.height(16.dp).fillMaxWidth().background(MaterialTheme.colorScheme
                    .background.copy(alpha = .2f)),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = formatDateTime(article.publishedAt)[0],
                fontSize = 8.sp,
                modifier = Modifier.padding(end = 16.dp, bottom = 1.dp),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MoreBottomSheet(
    article: Article,
    context:Context
) {
    val bgColor = if (isSystemInDarkTheme()) Color.Black else Color(0xFFE6E2DA)
    val contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val cardColor = if (isSystemInDarkTheme()) Color(0xFF1B2124) else Color(0xFFFDFDFD)
    Column (
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 12.dp)
            .background(bgColor),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
        ){
        val annotatedLinkString = buildAnnotatedString {
            val str = article.source.name
            val endIndex = str.length
            append(str)
            addStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                ), start = 0, end = endIndex
            )
            addStringAnnotation(
                tag = "Url",
                annotation = article.source.url,
                start = 0,
                end = str.length
            )
        }
        val annotatedArticleLinkString = buildAnnotatedString {
            val str = "Read Full Article"
            val endIndex = str.length
            append(str)
            addStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                ), start = 0, end = endIndex
            )
            addStringAnnotation(
                tag = "Url",
                annotation = article.url,
                start = 0,
                end = str.length
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)){
                Text(
                    text = "Source: ",
                    fontSize = 13.sp,
                    color = contentColor
                )
                ClickableText(
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    text = annotatedLinkString,
                    onClick = { offset ->
                        annotatedLinkString.getStringAnnotations(
                            tag = "Url",
                            start = offset,
                            end = offset
                        )
                            .firstOrNull()?.let { annotation ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                                context.startActivity(intent)
                            }
                    }
                )
            }
            val dateTimeArray = formatDateTime(article.publishedAt)
            Text(
                "On: ${dateTimeArray[0]}, ${dateTimeArray[1]}",
//                modifier = Modifier.weight(1f),
                fontSize = 13.sp,
                color = contentColor
                )
        }
        Text(
            text = article.title,
            color = contentColor,
            fontSize = 22.sp,
            lineHeight = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            softWrap = true,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Card(
            modifier = Modifier
                .height(225.dp)
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp, start = 4.dp, end = 4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(Color(0xFFF4F7FD)),
            elevation = CardDefaults.cardElevation(2.dp)
        ){
            AsyncImage(
                model = Uri.parse(article.image),
                contentScale = ContentScale.FillBounds,
                alignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
                contentDescription = "Image",
                placeholder = painterResource(R.drawable.bg_news),
                error = painterResource(R.drawable.bg_news)
            )
        }
        Card(
            colors = CardDefaults.cardColors(cardColor),
            modifier = Modifier.padding(start = 2.dp, end = 2.dp, top = 4.dp),
            shape = RoundedCornerShape(topEnd = 15.dp, topStart = 15.dp)
        ){
                Text(
                    text = "Description:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    softWrap = true,
                    color = contentColor.copy(alpha = .95f),
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 4.dp)
                )
                Text(
                    text = article.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    softWrap = true,
                    color = contentColor.copy(alpha = .6f),
                    lineHeight = 17.sp,
                    modifier = Modifier.padding( start = 12.dp, top = 4.dp, end = 6.dp)
                )
            Text(
                text = "Article:",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                softWrap = true,
                color = contentColor.copy(alpha = .95f),
                modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 4.dp)
            )
            Text(
                text = removeExtraChars(article.content),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                softWrap = true,
                fontFamily = FontFamily.SansSerif,
                lineHeight = 19.sp,
                color = contentColor.copy(alpha = .6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp, start = 12.dp, end = 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                contentAlignment = Alignment.Center
            ){
                ClickableText(
                    text = annotatedArticleLinkString,
                    onClick = { offset ->
                        annotatedArticleLinkString.getStringAnnotations(
                            tag = "Url",
                            start = offset,
                            end = offset
                        )
                            .firstOrNull()?.let { annotation ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                                context.startActivity(intent)
                            }
                    }
                )
            }
        }
    }
}

@Composable
fun TopBarWithDropdowns(
    selectedCategory: MutableState<String>,
    selectedCountry: MutableState<String>,
    selectedLanguage: MutableState<String>,
    selectedSort: MutableState<String>
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "NEWS4U",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = lobster_two_font,
            color = if (isSystemInDarkTheme()) Color.White else Color.Black
        )
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            item {
                DropdownMenuExample(label = "Category", items = Constants.categoryList, selectedItem = selectedCategory.value) { selectedCategory.value = it }
            }
            item {
                DropdownMenuExample(label = "Language", items = Constants.languageList, selectedItem = getLanguageNameFromCode(selectedLanguage.value)) { selectedLanguage.value = it }
            }
            item {
                DropdownMenuExample(label = "Country", items = Constants.countryList, selectedItem = getCountryNameFromCode(selectedCountry.value)) { selectedCountry.value = it }
            }
            item {
                DropdownMenuExample(label = "SortBy", items = listOf("relevance", "publishedAt"), selectedItem = selectedSort.value) { selectedSort.value = it }
            }
        }
    }
}

@Composable
fun DropdownMenuExample(
    label:String,
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ){
        val contentColor = if (isSystemInDarkTheme()) Color.White.copy(alpha = .8f) else Color
                .Black.copy(alpha = .8f)
        Text(label, fontWeight = FontWeight.Thin, fontSize = 10.sp, lineHeight = 14.sp, color = contentColor)
        OutlinedButton(
            onClick = { expanded = true },
            shape = RectangleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = selectedItem,
                fontFamily = dongle_font,
                color = if (isSystemInDarkTheme()) Color.White.copy(alpha = .9f) else Color.Black
                        .copy
                    (alpha = .9f)
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(
                    colors = MenuDefaults.itemColors(Color.Black),
                    text = { Text(
                        if (label=="Country") {
                        getCountryNameFromCode(item)
                    }else if(label=="Language") {
                        getLanguageNameFromCode(item)
                        }else
                         item )},
                    onClick = {
                    onItemSelected(item)
                    expanded = false
                })
            }
        }
    }
}

fun removeExtraChars(text: String): String {
    return text.replace(Regex("""\s\[\d+\schars]"""), "")
}

fun getCountryNameFromCode(countryCode: String): String {
    val locale = java.util.Locale("", countryCode.uppercase())
    return locale.displayCountry
}

fun getLanguageNameFromCode(languageCode: String): String {
    val locale = java.util.Locale(languageCode)
    return locale.displayLanguage
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateTime(dateTimeString:String):Array<String>{
    val dateTime = ZonedDateTime.parse(dateTimeString)

    val dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val formatedDate = dateTime.format(dateFormat)

    val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
    val formattedTime = dateTime.format(timeFormat)
    return arrayOf(formatedDate, formattedTime)
}

@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun Previeww(){
    TopBarWithDropdowns(mutableStateOf("any"), mutableStateOf("any"), mutableStateOf("any"), mutableStateOf("any"))
//    MoreBottomSheet(article = Article(
//        "Hello I am Kartik Maheswari Hello I am Kartik Maheswari ",
//        "Hello I am Kartik Maheswari",
//        "Hello I am Kartik Maheswari Maheswari Maheswari MaheswariMaheswariMaheswari MaheswariMaheswariMaheswariMaheswariMaheswariMaheswariMaheswariMaheswariMaheswariMaheswariMaheswariMaheswariMaheswariMaheswariMaheswariMaheswariMaheswariMaheswariMaheswari",
//        "",
//        "",
//        "2024-11-05T17:33:00Z",
//        Source("Kartik Maheshwari", "https://www.deccanchronicle.com")
//    ),
//        context = LocalContext.current)
//    NewsCard(article = Article("Hello", "Description", "content", " ", " ", " ", Source("", "")), {})
}