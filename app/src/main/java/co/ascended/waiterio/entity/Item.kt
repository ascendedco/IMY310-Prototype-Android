package co.ascended.waiterio.entity

data class Item(var id: Int, var name: String, var description: String,
                var category: Category, var available: Boolean,
                var quantity: Int, var price: Double) {

    fun clone(): Item = this.copy()
}