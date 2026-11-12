package com.mba.taf.data.model;

import java.util.List;

public record DataSet<T>(String description, List<T> cases) {
}
