const products = [
    {
        id: 1,
        name: "Handcrafted Beaded Bracelet",
        price: 299,
        oldPrice: 399,
        rating: "4.7",
        image: "assets/bracelet.jpg"
    },
    {
        id: 2,
        name: "Traditional Clay Pot",
        price: 449,
        oldPrice: 599,
        rating: "4.8",
        image: "assets/pot.jpg"
    },
    {
        id: 3,
        name: "Natural Jute Basket",
        price: 549,
        oldPrice: 699,
        rating: "4.6",
        image: "assets/basket.jpg"
    },
    {
        id: 4,
        name: "Handmade Wall Decor",
        price: 799,
        oldPrice: 999,
        rating: "4.9",
        image: "assets/decor-product.jpg"
    },
    {
        id: 5,
        name: "Artisan Necklace",
        price: 699,
        oldPrice: 899,
        rating: "4.7",
        image: "assets/necklace.jpg"
    },
    {
        id: 6,
        name: "Handcrafted Ceramic Vase",
        price: 599,
        oldPrice: 799,
        rating: "4.8",
        image: "assets/vase.jpg"
    },
    {
        id: 7,
        name: "Handmade Gift Box",
        price: 399,
        oldPrice: 499,
        rating: "4.5",
        image: "assets/gift.jpg"
    },
    {
        id: 8,
        name: "Artisan Tote Bag",
        price: 649,
        oldPrice: 799,
        rating: "4.6",
        image: "assets/tote.jpg"
    }
];


let cart = [];


/* DISPLAY PRODUCTS */

function displayProducts(productList = products) {

    const productGrid = document.getElementById("productGrid");

    productGrid.innerHTML = "";

    productList.forEach(product => {

        const card = document.createElement("div");

        card.className = "product-card";

        card.innerHTML = `
            <div 
                class="product-image"
                style="background-image: url('${product.image}')">
            </div>

            <div class="product-info">

                <h3>${product.name}</h3>

                <div class="rating">
                    ★ ${product.rating}
                </div>

                <div>
                    <span class="price">
                        ₹${product.price}
                    </span>

                    <span class="old-price">
                        ₹${product.oldPrice}
                    </span>
                </div>

                <button 
                    class="add-cart"
                    onclick="addToCart(${product.id})">
                    Add to Cart
                </button>

            </div>
        `;

        productGrid.appendChild(card);
    });
}


/* ADD TO CART */

function addToCart(productId) {

    const product = products.find(
        item => item.id === productId
    );

    const existingProduct = cart.find(
        item => item.id === productId
    );

    if (existingProduct) {

        existingProduct.quantity++;

    } else {

        cart.push({
            ...product,
            quantity: 1
        });

    }

    updateCart();

    openCart();
}


/* UPDATE CART */

function updateCart() {

    const cartItems = document.getElementById("cartItems");

    const cartCount = document.getElementById("cartCount");

    const cartTotal = document.getElementById("cartTotal");


    let totalItems = 0;

    let totalPrice = 0;


    cart.forEach(item => {

        totalItems += item.quantity;

        totalPrice += item.price * item.quantity;

    });


    cartCount.textContent = totalItems;

    cartTotal.textContent = totalPrice;


    if (cart.length === 0) {

        cartItems.innerHTML = `
            <p class="empty-cart">
                Your cart is empty.
            </p>
        `;

        return;
    }


    cartItems.innerHTML = "";


    cart.forEach(item => {

        const cartItem = document.createElement("div");

        cartItem.style.padding = "15px 0";

        cartItem.style.borderBottom = "1px solid #eee";


        cartItem.innerHTML = `

            <strong>
                ${item.name}
            </strong>

            <p style="margin-top:8px;">
                ₹${item.price} × ${item.quantity}
            </p>

            <div style="margin-top:10px;">

                <button
                    onclick="changeQuantity(${item.id}, -1)"
                    style="padding:5px 10px;">
                    −
                </button>

                <span style="margin:0 10px;">
                    ${item.quantity}
                </span>

                <button
                    onclick="changeQuantity(${item.id}, 1)"
                    style="padding:5px 10px;">
                    +
                </button>

                <button
                    onclick="removeFromCart(${item.id})"
                    style="margin-left:15px; padding:5px 10px;">
                    Remove
                </button>

            </div>
        `;

        cartItems.appendChild(cartItem);

    });

}


/* CHANGE QUANTITY */

function changeQuantity(productId, change) {

    const item = cart.find(
        product => product.id === productId
    );

    if (!item) return;


    item.quantity += change;


    if (item.quantity <= 0) {

        cart = cart.filter(
            product => product.id !== productId
        );

    }


    updateCart();
}


/* REMOVE PRODUCT */

function removeFromCart(productId) {

    cart = cart.filter(
        product => product.id !== productId
    );

    updateCart();
}


/* CART OPEN */

function openCart() {

    document
        .getElementById("cartPanel")
        .classList.add("active");

}


/* CART CLOSE */

function closeCart() {

    document
        .getElementById("cartPanel")
        .classList.remove("active");

}


/* LOGIN */

function showLogin() {

    document
        .getElementById("loginModal")
        .classList.add("active");

}


function closeLogin() {

    document
        .getElementById("loginModal")
        .classList.remove("active");

}


function loginUser() {

    const email =
        document.getElementById("loginEmail").value.trim();

    const password =
        document.getElementById("loginPassword").value.trim();


    if (!email || !password) {

        alert("Please enter email and password.");

        return;
    }


    alert("Login successful!");

    closeLogin();

}


/* SEARCH */

function searchProducts() {

    const searchText =
        document
            .getElementById("searchInput")
            .value
            .toLowerCase()
            .trim();


    if (!searchText) {

        displayProducts();

        return;
    }


    const filteredProducts = products.filter(product =>
        product.name.toLowerCase().includes(searchText)
    );


    displayProducts(filteredProducts);


    document
        .getElementById("products")
        .scrollIntoView({
            behavior: "smooth"
        });

}


/* SHOP NOW */

function scrollToProducts() {

    document
        .getElementById("products")
        .scrollIntoView({
            behavior: "smooth"
        });

}


/* CHECKOUT */

function checkout() {

    if (cart.length === 0) {

        alert("Your cart is empty.");

        return;
    }


    alert(
        "Checkout page will be connected next."
    );

}


/* LOAD PRODUCTS */

displayProducts();
updateCart();