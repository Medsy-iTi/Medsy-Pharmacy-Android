import com.medsy.data.orders.model.OrderDetailsDto
import com.medsy.data.orders.model.OrderItemDto
import com.medsy.data.orders.model.OrderPageResponseDto
import com.medsy.domain.orders.model.OrderDetailsDomain
import com.medsy.domain.orders.model.OrderItemDomain
import com.medsy.domain.orders.model.OrderPageDomain

fun OrderPageResponseDto.toDomain(): OrderPageDomain {
    return OrderPageDomain(
        content = content.map { it.toDomain() },
        pageNumber = pageNumber,
        pageSize = pageSize,
        totalElements = totalElements,
        totalPages = totalPages,
        last = last
    )
}

fun OrderDetailsDto.toDomain(): OrderDetailsDomain {
    return OrderDetailsDomain(
        id = id,
        customerId = customerId,
        deliveryLatitude = deliveryLatitude,
        deliveryLongitude = deliveryLongitude,
        deliveryAddress = deliveryAddress,
        status = status,
        createdAt = createdAt,
        items = items.map { it.toDomain() }
    )
}

fun OrderItemDto.toDomain(): OrderItemDomain {
    return OrderItemDomain(
        id = id,
        productId = productId,
        quantity = quantity
    )
}