package com.aca.people.presentation.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aca.people.R
import com.aca.people.domain.model.CategoryFilter
import com.aca.people.domain.model.GenderFilter
import com.aca.people.domain.model.UserFilterCriteria

@Composable
fun FilterBar(
    criteria: UserFilterCriteria,
    onCategorySelected: (CategoryFilter) -> Unit,
    onGenderSelected: (GenderFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = criteria.category == CategoryFilter.ALL,
                onClick = { onCategorySelected(CategoryFilter.ALL) },
                label = { Text(text = stringResource(id = R.string.filter_all)) }
            )
        }
        item {
            FilterChip(
                selected = criteria.category == CategoryFilter.FAVORITES,
                onClick = { onCategorySelected(CategoryFilter.FAVORITES) },
                label = { Text(text = stringResource(id = R.string.filter_favorites)) }
            )
        }
        item {
            FilterChip(
                selected = criteria.category == CategoryFilter.WITH_NOTES,
                onClick = { onCategorySelected(CategoryFilter.WITH_NOTES) },
                label = { Text(text = stringResource(id = R.string.filter_with_notes)) }
            )
        }
        item {
            val isFemale = criteria.gender == GenderFilter.FEMALE
            FilterChip(
                selected = isFemale,
                onClick = {
                    onGenderSelected(if (isFemale) GenderFilter.ALL else GenderFilter.FEMALE)
                },
                label = { Text(text = stringResource(id = R.string.filter_female)) }
            )
        }
        item {
            val isMale = criteria.gender == GenderFilter.MALE
            FilterChip(
                selected = isMale,
                onClick = {
                    onGenderSelected(if (isMale) GenderFilter.ALL else GenderFilter.MALE)
                },
                label = { Text(text = stringResource(id = R.string.filter_male)) }
            )
        }
    }
}
