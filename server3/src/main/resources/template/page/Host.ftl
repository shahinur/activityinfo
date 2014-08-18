<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
    <meta name="description" content="">

    <title>${domain.title}</title>

    <script type="text/javascript">
        if (document.cookie.indexOf('authToken=') == -1 ||
                document.cookie.indexOf('userId') == -1 ||
                document.cookie.indexOf('email') == -1) {
            window.location = "/login" + window.location.hash;
        }
        var ClientContext = {
            version: '$[display.version]',
            commitId: '$[git.commit.id]',
            title: '${domain.title}'

        };
    </script>
    <script type="text/javascript" language="javascript" src="AI/AI.nocache.js"></script>
</head>

<body>

<!-- Preloader -->
<div id="boot-preloader">
    <div id="status"><i class="fa fa-spinner fa-spin"></i></div>
</div>

<section id="root">


</section>
</body>
</html>