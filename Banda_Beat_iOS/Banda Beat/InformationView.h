//
//  InformationView.h
//  Banda Beat
//
//  Created by Iker Mendilibar on 05/11/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface InformationView : UIView
@property (strong, nonatomic) UIView *messageView;
@property (strong, nonatomic) UIImageView *imageView;
@property (strong, nonatomic) UILabel *bodyText;
@property (strong, nonatomic) UIButton *buttonHelp, *buttonCancel;
@property (assign, nonatomic) BOOL isVisible;
-(void)showMessage;
-(id)initWithFrame:(CGRect)frame message:(NSString*)message;
-(id)initWithFrame:(CGRect)frame message:(NSString*)message withController:(UIViewController*)controller;
@end
