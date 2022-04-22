package com.example.stockmarket.presentation.companyInfo

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stockmarket.R
import com.example.stockmarket.ui.theme.Orange
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun CompanyInfoScreen(
    symbol: String,
    viewModel: CompanyInfoViewModel = hiltViewModel()
) {
    val state = viewModel.state

    if (state.error == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            state.company?.let { company ->
                Text(
                    text = company.name,
                    fontWeight = FontWeight.Bold,
                    color = Orange,
                    fontSize = 18.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = company.symbol,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Divider(modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.industry, company.industry),
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.country, company.country),
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Divider(modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.description, company.description),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                if (state.stockInfoList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.market_summary),
                        color = Orange
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    StockChart(
                        infoList = state.stockInfoList,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .align(CenterHorizontally)
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Center,
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else if (state.error != null) {
            Text(
                text = state.error.asString(),
                color = MaterialTheme.colors.error
            )
        }
    }
}
