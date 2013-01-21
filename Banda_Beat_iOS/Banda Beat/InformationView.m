//
//  InformationView.m
//  Banda Beat
//
//  Created by Iker Mendilibar on 05/11/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "InformationView.h"
#import "HTMLViewController.h"
#import <QuartzCore/QuartzCore.h>

@implementation InformationView


-(id)initWithFrame:(CGRect)frame message:(NSString*)message
{
    self = [super initWithFrame:frame];
    if (self) {
        [self baseInitWithMessage:message];
    }
    return self;
}

-(id)initWithFrame:(CGRect)frame message:(NSString*)message withController:(UIViewController*)controller
{
    self = [super initWithFrame:frame];
    if (self) {
        [self baseInitWithMessage:message withController:controller];
    }
    return self;
}

- (void)baseInitWithMessage:(NSString*)message
{
    
    //self.backgroundColor = [UIColor colorWithPatternImage:[self addBackground]];
    //self.alpha = 0.4;
    
    self.opaque = NO;
    
    _messageView = [[UIView alloc] initWithFrame:CGRectMake(self.frame.size.width / 2 - 130, -200, 260, 230)];
    _messageView.backgroundColor = [UIColor whiteColor];
    _messageView.layer.borderColor = [UIColor blackColor].CGColor;
    
    _bodyText = [[UILabel alloc] initWithFrame:CGRectMake(self.messageView.frame.size.width / 2 - 125, self.messageView.frame.size.height / 2, 250, 90)];
    _bodyText.textColor = [UIColor blackColor];
    _bodyText.numberOfLines = 0;
    _bodyText.textAlignment = NSTextAlignmentCenter;
    _bodyText.text = message;
    
    __weak UIImage * image = [UIImage imageNamed:@"logo"];
    
    _imageView = [[UIImageView alloc] initWithFrame:CGRectMake(self.messageView.frame.size.width / 2 - image.size.width / 2, self.messageView.frame.size.height / 2 - image.size.height, image.size.width, image.size.height)];
    _imageView.image = image;

    _messageView.layer.borderColor = [UIColor blackColor].CGColor;
    _messageView.layer.borderWidth = 1.0;
    _messageView.layer.cornerRadius = 10.0;
    
    [_messageView addSubview:_imageView];
    [_messageView addSubview:_bodyText];

}

- (void)baseInitWithMessage:(NSString*)message withController:(UIViewController*)controller
{
    self.opaque = NO;
    
    _messageView = [[UIView alloc] initWithFrame:CGRectMake(self.frame.size.width / 2 - 130, -200, 260, 230)];
    _messageView.backgroundColor = [UIColor whiteColor];
    _messageView.layer.borderColor = [UIColor blackColor].CGColor;
    
    _bodyText = [[UILabel alloc] initWithFrame:CGRectMake(self.messageView.frame.size.width / 2 - 125, self.messageView.frame.size.height / 2, 250, 90)];
    _bodyText.textColor = [UIColor blackColor];
    _bodyText.numberOfLines = 0;
    _bodyText.textAlignment = NSTextAlignmentCenter;
    _bodyText.text = message;
    
    UIImage *buttonImage = [[UIImage imageNamed:@"boton"]
                            resizableImageWithCapInsets:UIEdgeInsetsMake(5, 5, 5, 5)];

    
    /*_buttonHelp = [[UIButton alloc] initWithFrame:CGRectMake(self.messageView.frame.size.width / 2 - 60, self.messageView.frame.size.height / 2 + 75, 120, 30)];
    [_buttonHelp addTarget:controller action:@selector(help:) forControlEvents:UIControlEventTouchUpInside];
    [_buttonHelp setBackgroundImage:buttonImage forState:UIControlStateNormal];
    
    [_buttonHelp setTitle:@"Laguntza" forState:UIControlStateNormal];
    [_buttonHelp setTitle:@"Laguntza" forState:UIControlStateHighlighted];*/
    
    
    _buttonCancel = [[UIButton alloc] initWithFrame:CGRectMake(self.messageView.frame.size.width / 2 - 60, self.messageView.frame.size.height / 2 + 75, 120, 30)];
    [_buttonCancel addTarget:controller action:@selector(alertCancel) forControlEvents:UIControlEventTouchUpInside];
    
    [_buttonCancel setBackgroundImage:buttonImage forState:UIControlStateNormal];
    
    [_buttonCancel setTitle:@"Ok" forState:UIControlStateNormal];
    [_buttonCancel setTitle:@"Ok" forState:UIControlStateHighlighted];
    
    __weak UIImage * image = [UIImage imageNamed:@"logo"];
    
    _imageView = [[UIImageView alloc] initWithFrame:CGRectMake(self.messageView.frame.size.width / 2 - image.size.width / 2, self.messageView.frame.size.height / 2 - image.size.height, image.size.width, image.size.height)];
    _imageView.image = image;
    
    _messageView.layer.borderColor = [UIColor blackColor].CGColor;
    _messageView.layer.borderWidth = 1.0;
    _messageView.layer.cornerRadius = 10.0;
    
    [_messageView addSubview:_imageView];
    [_messageView addSubview:_bodyText];
    //[_messageView addSubview:_buttonHelp];
    [_messageView addSubview:_buttonCancel];
    
}


-(void)showMessage
{
    //Lar cordenadas estar hardcoded. Esto no se deberia de hacer, pero no hay tiempo
    [self addSubview:_messageView];
    
    CGRect informationFrame;
    informationFrame.size = self.messageView.frame.size;

    
    informationFrame.origin.y = self.bounds.size.height / 2 - 100;
    informationFrame.origin.x = self.messageView.frame.origin.x;
    
    
    
    [UIView animateWithDuration:0.25
                          delay:0.25
                        options: UIViewAnimationCurveLinear
                     animations:^{
                         self.messageView.frame = informationFrame;
                     }
                     completion:^(BOOL finished){
                         self.isVisible = TRUE;
                     }];

}

- (void)drawRect:(CGRect)rect
{   
    CGContextRef context = UIGraphicsGetCurrentContext();

    UIGraphicsBeginImageContextWithOptions(self.bounds.size, YES, 1);
	// Our gradient only has two locations - start and finish. More complex gradients might have more colours
    size_t num_locations = 2;
	// The location of the colors is at the start and end
    CGFloat locations[2] = { 0.0, 1.0 };
	// These are the colors! That's two RBGA values
    CGFloat components[8] = {
        0.4,0.4,0.4, 0.8,
        0.1,0.1,0.1, 0.5 };
	// Create a color space
    CGColorSpaceRef myColorspace = CGColorSpaceCreateDeviceRGB();
	// Create a gradient with the values we've set up
    CGGradientRef myGradient = CGGradientCreateWithColorComponents (myColorspace, components, locations, num_locations);
	// Set the radius to a nice size, 80% of the width. You can adjust this
    float myRadius = (self.bounds.size.width*.8)/2;
	// Now we draw the gradient into the context. Think painting onto the canvas
    CGContextDrawRadialGradient (context, myGradient, self.center, 0, self.center, myRadius, kCGGradientDrawsAfterEndLocation);

    CGContextSetAlpha(context, 0.4);

    //CGContextAddPath(context, roundRectPath);
    CGContextFillPath(context);
    CGGradientRelease(myGradient);
    CGColorSpaceRelease(myColorspace);

}

/*CGPathRef NewPathWithRoundRect(CGRect rect, CGFloat cornerRadius)
{
    //
    // Create the boundary path
    //
    CGMutablePathRef path = CGPathCreateMutable();
    CGPathMoveToPoint(path, NULL,
                      rect.origin.x,
                      rect.origin.y + rect.size.height - cornerRadius);
    
    // Top left corner
    CGPathAddArcToPoint(path, NULL,
                        rect.origin.x,
                        rect.origin.y,
                        rect.origin.x + rect.size.width,
                        rect.origin.y,
                        cornerRadius);
    
    // Top right corner
    CGPathAddArcToPoint(path, NULL,
                        rect.origin.x + rect.size.width,
                        rect.origin.y,
                        rect.origin.x + rect.size.width,
                        rect.origin.y + rect.size.height,
                        cornerRadius);
    
    // Bottom right corner
    CGPathAddArcToPoint(path, NULL,
                        rect.origin.x + rect.size.width,
                        rect.origin.y + rect.size.height,
                        rect.origin.x,
                        rect.origin.y + rect.size.height,
                        cornerRadius);
    
    // Bottom left corner
    CGPathAddArcToPoint(path, NULL,
                        rect.origin.x,
                        rect.origin.y + rect.size.height,
                        rect.origin.x,
                        rect.origin.y,
                        cornerRadius);
    
    // Close the path at the rounded rect
    CGPathCloseSubpath(path);
    
    return path;
    
}*/


@end
