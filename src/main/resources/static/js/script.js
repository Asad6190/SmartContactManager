console.log("This is script file")

const toggleSidebar = () => {
    const sidebar = $(".sidebar");
    const content = $(".content");

    // Toggle visibility using a class for better maintainability
    sidebar.toggleClass("hidden");

    // Adjust content margin-left based on sidebar visibility
    content.css("margin-left", sidebar.hasClass("hidden") ? "0%" : "20%");
};


const search = () => {
    // console.log("searching..")

    let query = $("#search-input").val();

    if (query === "") {
        $(".search-result").hide();
        // Optionally add message for empty search
    } else {
        console.log(query);

        // Sending request to server
        let url = `http://localhost:8080/search/${query}`; // Template literal for URL

        fetch(url)
            .then((response) => response.json())
            .then((data) => {
                console.log(data);

                let text = `<div class="list-group">`;

                data.forEach((contact) => {
                    text += `
          <a href="/user/${contact.cId}/contact" class="list-group-item list-group-action">
            ${contact.name}
          </a>`;
                });

                text += `</div>`;

                $(".search-result").html(text);
                $(".search-result").show();
            })
            .catch((error) => {
                // Handle errors here, e.g., display an error message
                console.error("Error fetching data:", error);
            });
    }
};